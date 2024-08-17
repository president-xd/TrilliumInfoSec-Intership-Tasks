# CSV File Injection Vulnerability

## Overview

This document describes a CSV file injection vulnerability discovered in our web application. The vulnerability was triggered due to the lack of validation on CSV files, allowing an attacker to inject malicious formulas through the "description" field.

# CSV File Injection Vulnerability

## Overview

This document provides details about a CSV file injection vulnerability discovered in our web application's file upload feature. The vulnerability allowed attackers to inject and execute malicious commands through the CSV file's "address" field.

## Vulnerability Description

### Upload Feature

The upload functionality was handled by the `upload.php` file located in server.


This file processed CSV uploads without proper validation, particularly on the "address" field, which allowed command injection.

### Payload Example

Here is an example of a malicious `Payload.csv` file that could be uploaded to exploit the vulnerability:

```csv
student name,father name,address
Emma Brown,Chris Doe,"=cmd|'systeminfo'"
Noah Davis,Andy Smith,"=cmd|'whoami'"
Liam Wilson,Joseph Lee,"=cmd|'ipconfig'"
Olivia Martin,Samuel Hall,"=cmd|'netstat'"
Ava White,David Johnson,"=cmd|'dir'"
Mia Harris,Ethan Clark,"=cmd|'dir /B'"
James Anderson,Kevin Lee,"=cmd|'tasklist'"
Benjamin Carter,Daniel Hill,"=cmd|'echo Custom message from server > log.txt'"
```
In this example, the `address` field contains commands that will execute when the CSV file is opened in a spreadsheet application like Excel.

## Exploit
### Steps to Exploit
1. **Crafting the Malicious CSV:**

- The attacker prepares a CSV file with malicious formulas in the "address" field.
- The crafted file `Payload.csv` looks like the example above.

2. **Uploading the CSV File:**

- The attacker uploads `Payload.csv` via the vulnerable upload interface provided by `upload.php`.

3. Triggering the Vulnerability:

- When a user opens the uploaded CSV file in a spreadsheet application, the formulas in the "address" field execute, potentially compromising the user's system.

## Code Reference: `upload.php`
The upload.php file contained the following logic for handling file uploads:
```php
<?php
$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

// Check if file is a valid CSV
if($imageFileType != "csv") {
    echo "Sorry, only CSV files are allowed.";
    exit;
}

// Attempt to upload file
if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
    echo "The file ". basename($_FILES["fileToUpload"]["name"]). " has been uploaded.";
} else {
    echo "Sorry, there was an error uploading your file.";
}
?>
```

## Mitigation Strategies
To mitigate this vulnerability, the following steps should be taken:

1. Enhanced Field Validation:

- Ensure that the CSV file not only contains the required fields but also validates the content of these fields to prevent formula injection.
2. Sanitize Input:

- Sanitize and escape any user input that could be interpreted as a formula by spreadsheet applications.
3. Secure Parsing Libraries:

- Utilize secure libraries that handle CSV parsing and prevent execution of dangerous content.

## Conclusion
CSV file injection is a serious vulnerability that can lead to remote code execution when exploited. By implementing proper validation and sanitization measures, such vulnerabilities can be effectively mitigated, ensuring the security of the application.

## Further Reading
- [OWASP CSV Injection](https://owasp.org/www-community/attacks/CSV_Injection)
- [CSV Injection](https://medium.com/@president-xd/csv-injection-vulnerability-formula-injection-2db8cbd8aa67?source=user_profile---------0----------------------------)
