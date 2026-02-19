package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class SaldeoSmartHttpClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final Map<Class<?>, JAXBContext> JAXB_CONTEXTS = new ConcurrentHashMap<>();

    private final HttpClient httpClient;
    private final String baseUrl;
    private final String username;
    private final String apiToken;

    SaldeoSmartHttpClient(String baseUrl, String username, String apiToken) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.username = username;
        this.apiToken = apiToken;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * GET request (e.g. invoice.list, contractor.list, company.list).
     *
     * @param path         API path, e.g. "/api/xml/3.0/invoice/list"
     * @param params       extra query params (company_program_id, policy, etc.)
     * @param responseType JAXB-annotated response class
     */
    <T> T get(String path, Map<String, String> params, Class<T> responseType) {
        Map<String, String> allParams = new TreeMap<>(params);
        allParams.put("username", username);
        allParams.put("req_id", generateReqId());

        String reqSig = calculateSignature(allParams);
        allParams.put("req_sig", reqSig);

        String queryString = buildQueryString(allParams);
        String url = baseUrl + path + "?" + queryString;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept-Encoding", "gzip, deflate")
                    .GET()
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            checkStatus(response);
            return unmarshalResponse(response, responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("GET " + path + " failed", e);
        }
    }

    /**
     * POST request with XML command body (e.g. invoice.add, invoice.getidlist).
     *
     * @param path         API path, e.g. "/api/xml/3.0/invoice/add"
     * @param params       extra form params (company_program_id, etc.)
     * @param command      JAXB-annotated object to marshal as XML command
     * @param responseType JAXB-annotated response class
     */
    <T> T post(String path, Map<String, String> params, Object command, Class<T> responseType) {
        String encodedCommand = marshalAndEncode(command);

        Map<String, String> allParams = new TreeMap<>(params);
        allParams.put("username", username);
        allParams.put("req_id", generateReqId());
        allParams.put("command", encodedCommand);

        String reqSig = calculateSignature(allParams);
        allParams.put("req_sig", reqSig);

        String formBody = buildQueryString(allParams);
        String url = baseUrl + path;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            checkStatus(response);
            return unmarshalResponse(response, responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("POST " + path + " failed", e);
        }
    }

    /**
     * Download raw bytes from a URL (e.g. invoice PDF from SOURCE link).
     */
    byte[] downloadBytes(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() / 100 != 2) {
                throw new RuntimeException("HTTP " + response.statusCode() + " downloading " + url);
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Download failed: " + url, e);
        }
    }

    // --- Signature calculation ---

    /**
     * req_sig = HEX(MD5(URL_ENCODE(sorted key=value pairs) + api_token))
     */
    String calculateSignature(Map<String, String> params) {
        TreeMap<String, String> sorted = new TreeMap<>(params);
        StringBuilder base = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            base.append(entry.getKey()).append("=").append(entry.getValue());
        }
        String encoded = URLEncoder.encode(base.toString(), StandardCharsets.UTF_8);
        String toHash = encoded + apiToken;
        return md5Hex(toHash);
    }

    // --- XML marshalling / encoding ---

    /**
     * Marshal JAXB object to XML, gzip, base64-encode.
     */
    String marshalAndEncode(Object jaxbObject) {
        try {
            String xml = marshalToXml(jaxbObject);
            byte[] gzipped = gzipCompress(xml.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(gzipped);
        } catch (Exception e) {
            throw new RuntimeException("Failed to marshal and encode command", e);
        }
    }

    String marshalToXml(Object jaxbObject) {
        try {
            JAXBContext context = getContext(jaxbObject.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            StringWriter writer = new StringWriter();
            marshaller.marshal(jaxbObject, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("XML marshalling failed", e);
        }
    }

    // --- Response handling ---

    private <T> T unmarshalResponse(HttpResponse<InputStream> response, Class<T> responseType) {
        try (InputStream body = decompressIfNeeded(response)) {
            JAXBContext context = getContext(responseType);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            @SuppressWarnings("unchecked")
            T result = (T) unmarshaller.unmarshal(body);
            return result;
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Failed to unmarshal response", e);
        }
    }

    private InputStream decompressIfNeeded(HttpResponse<InputStream> response) throws IOException {
        String contentEncoding = response.headers()
                .firstValue("Content-Encoding").orElse("");
        if (contentEncoding.contains("gzip")) {
            return new GZIPInputStream(response.body());
        }
        return response.body();
    }

    private void checkStatus(HttpResponse<?> response) {
        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + response.statusCode());
        }
    }

    // --- Utilities ---

    private static String generateReqId() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
              .append("=")
              .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private static byte[] gzipCompress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(data);
        }
        return baos.toByteArray();
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    private static JAXBContext getContext(Class<?> clazz) {
        return JAXB_CONTEXTS.computeIfAbsent(clazz, c -> {
            try {
                return JAXBContext.newInstance(c);
            } catch (JAXBException e) {
                throw new RuntimeException("Failed to create JAXBContext for " + c.getName(), e);
            }
        });
    }
}
