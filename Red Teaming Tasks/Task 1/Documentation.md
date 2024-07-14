
## Task 1: Our task one was create a page that upload files without extension validation

# Formal Documentation

Hello, I am Mohsin Mukhtiar, Red Teaming intern at NISS, Pakistan.

This task is going to be on File Uploading Vulnerability, So first we need to know what is File Uploading Vulnerability.

## File Uploading Vulnerability
File upload vulnerabilities are when a web server allows users to upload files to its filesystem without sufficiently validating things like their name, type, contents, or size. Failing to properly enforce restrictions on these could mean that even a basic image upload function can be used to upload arbitrary and potentially dangerous files instead. This could even include server-side script files that enable remote code execution.

In some cases, the act of uploading the file is in itself enough to cause damage. Other attacks may involve a follow-up HTTP request for the file, typically to trigger its execution by the server.

## What is the impact of file upload vulnerabilities?
The impact of file upload vulnerabilities generally depends on two key factors:
- Which aspect of the file the website fails to validate properly, whether that be its size, type, contents, and so on.
- What restrictions are imposed on the file once it has been successfully uploaded.

In the worst case scenario, the file's type isn't validated properly, and the server configuration allows certain types of file (such as .php and .jsp) to be executed as code. In this case, an attacker could potentially upload a server-side code file that functions as a web shell, effectively granting them full control over the server.

If the filename isn't validated properly, this could allow an attacker to overwrite critical files simply by uploading a file with the same name. If the server is also vulnerable to directory traversal, this could mean attackers are even able to upload files to unanticipated locations.

Failing to make sure that the size of the file falls within expected thresholds could also enable a form of denial-of-service (DoS) attack, whereby the attacker fills the available disk space.

## Codes

### Index.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>
</head>
<body>
    <h1>Welcome to the Main Page</h1>
    <p>This is the main page of the website.</p>
</body>
</html>

```

### Panel.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Page</title>
</head>
<body>
    <h1>Welcome to the Panel Page</h1>
    <p>This is the panel page of the website.</p>
    
    <form action="upload.php" method="post" enctype="multipart/form-data">
        <label for="fileToUpload">Select file to upload:</label>
        <input type="file" name="fileToUpload" id="fileToUpload">
        <input type="submit" value="Upload File" name="submit">
    </form>
</body>
</html>
```

### Uploads.php
```php
<?php
$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$uploadOk = 1;

// Check if file already exists
if (file_exists($target_file)) {
    echo "Sorry, file already exists.";
    $uploadOk = 0;
}

// Check file size (optional)
if ($_FILES["fileToUpload"]["size"] > 5000000) {
    echo "Sorry, your file is too large.";
    $uploadOk = 0;
}

// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo "Sorry, your file was not uploaded.";
// If everything is ok, try to upload file
} else {
    if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
        echo "The file ". htmlspecialchars(basename($_FILES["fileToUpload"]["name"])). " has been uploaded.";
    } else {
        echo "Sorry, there was an error uploading your file.";
    }
}
?>
```
#### NOTE : As you can see in the code that there is no extension validation, So it can exploited easily. If there was any limitation of uploading php file. So we can upload in other extension like,

```
php3
php4
php5
phtml
```

### Uploads.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Uploads Page</title>
</head>
<body>
    <h1>Welcome to the Uploads Page</h1>
    <p>Here are the uploaded files:</p>
    <ul>
        <?php
        $dir = "uploads/";
        if (is_dir($dir)) {
            if ($dh = opendir($dir)) {
                while (($file = readdir($dh)) !== false) {
                    if ($file != "." && $file != "..") {
                        echo "<li><a href='uploads/$file'>$file</a></li>";
                    }
                }
                closedir($dh);
            }
        }
        ?>
    </ul>
</body>
</html>

```

## Approoch (My Approach)
![Screenshot from 2024-07-14 22-00-15](https://github.com/user-attachments/assets/2177d858-a0ee-4b3b-87d4-cf4c43b2adcb)

This was the home page, so after that we can run gobuster or other directory enumeration tool or technique to find hidden directories on the web server.
```shell
gobuster dir -u http://example.com -w /usr/share/wordlists/rockyou.txt
```
Then you can get the directories of this website, like in my case I found these two crucial directories Panel and Uploads.

When I open the http://machine_ip/panel.html, this page was opened.

![Screenshot from 2024-07-14 22-10-36](https://github.com/user-attachments/assets/b347375d-618a-4cb5-ae08-af6981f51e6b)

Then I uploaded php-reverse shell, and it was successfully uploaded on the web server.

![Screenshot from 2024-07-14 22-11-24](https://github.com/user-attachments/assets/93eb7773-6072-49fe-91d2-eb30d511b89a)

So, for confirming this I went to http://machine_ip/uploads, and as I said that I was successfull in uploading the php-reverse shell.

![Screenshot from 2024-07-14 22-11-42](https://github.com/user-attachments/assets/8d4a012d-3377-4295-8303-01c56dfb2314)

Then there are different ways to get access to victim machine like you can get by manually clicking the reverse shell, or by the server can also execute the file and you can get the reverse shell.

## Exploiting the Vulnerability
For exploiting this vulnerability the user will perform following steps:
1. Scanning IP for open ports.
2. If HTTP's and SSH's port is open.
3. It will start exploring the site, like viewing its source or code, framework and other things including directory enumeration.
4. Then It will find a source where he can upload reverse shell.
5. If the user is able to get the reverse shell, it will get into victim's system.
6. Then He can do several things, like he will be root user and try to change different config files and other things too.

## Mitigation
This vulnerability can be mitigated when:
- Check the file extension against a whitelist of permitted extensions rather than a blacklist of prohibited ones. It's much easier to guess which extensions you might want to allow than it is to guess which ones an attacker might try to upload.
Make sure the filename doesn't contain any substrings that may be interpreted as a directory or a traversal sequence (../).
- Rename uploaded files to avoid collisions that may cause existing files to be overwritten.
- Do not upload files to the server's permanent filesystem until they have been fully validated.
- As much as possible, use an established framework for preprocessing file uploads rather than attempting to write your own validation mechanisms.




