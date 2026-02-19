# Invoicing SaldeoSMART

[SaldeoSMART](https://saldeosmart.pl) implementation of the [invoicing-api](https://github.com/commerce-link/invoicing-api) provider interface.

Supports creating and fetching invoices, fetching PDFs, and looking up billing parties via the SaldeoSMART XML API.

## Provider Discovery

This library registers itself for `ServiceLoader` discovery. Add it to your classpath and the provider will be available automatically via `InvoicingProviderDescriptor` SPI. See the [provider-api README](https://github.com/commerce-link/provider-api) for details.

## Configuration Fields

| Key                | Label              | Type     | Required |
|--------------------|--------------------|----------|----------|
| `apiUrl`           | API URL            | TEXT     | yes      |
| `username`         | Username           | TEXT     | yes      |
| `apiToken`         | API Token          | PASSWORD | yes      |
| `companyProgramId` | Company Program ID | TEXT     | yes      |
