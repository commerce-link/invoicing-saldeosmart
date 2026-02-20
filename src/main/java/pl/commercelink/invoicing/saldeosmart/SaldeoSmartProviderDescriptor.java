package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.InvoicingProvider;
import pl.commercelink.invoicing.api.InvoicingProviderDescriptor;
import pl.commercelink.provider.api.ProviderField;

import java.util.List;
import java.util.Map;

public class SaldeoSmartProviderDescriptor implements InvoicingProviderDescriptor {

    @Override
    public String name() {
        return "saldeosmart";
    }

    @Override
    public String displayName() {
        return "SaldeoSMART";
    }

    @Override
    public List<ProviderField> configurationFields() {
        return List.of(
                new ProviderField("apiUrl", "API URL", ProviderField.FieldType.TEXT, true, "https://saldeo.brainshare.pl"),
                new ProviderField("username", "Username", ProviderField.FieldType.TEXT, true, ""),
                new ProviderField("apiToken", "API Token", ProviderField.FieldType.PASSWORD, true, ""),
                new ProviderField("companyProgramId", "Company Program ID", ProviderField.FieldType.TEXT, true, "")
        );
    }

    @Override
    public InvoicingProvider create(Map<String, String> configuration) {
        String apiUrl = configuration.get("apiUrl");
        String username = configuration.get("username");
        String apiToken = configuration.get("apiToken");
        String companyProgramId = configuration.get("companyProgramId");
        return new SaldeoSmartInvoicingProvider(apiUrl, username, apiToken, companyProgramId);
    }
}
