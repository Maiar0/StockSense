/**
 * CSVUtils
 *
 * A utility class for handling CSV file operations, including exporting and importing `Item` objects.
 * This class leverages the OpenCSV library for efficient CSV parsing and writing.
 *
 * Functionality:
 * - Export a list of items to a CSV file with a predefined structure.
 * - Import a list of items from a CSV file, ensuring data integrity and proper parsing.
 *
 * Usage:
 * - Use `exportToCSV` to save a list of `Item` objects to a file.
 * - Use `importFromCSV` to read and parse items from a CSV file.
 *
 * Dependencies:
 * - OpenCSV library for CSV file operations.
 * - `Item` model class for representing item data.
 *
 * Error Handling:
 * - Provides detailed error messages for invalid input, file I/O issues, or CSV formatting errors.
 *
 * Example:
 * ```java
 * // Exporting items to CSV
 * List<Item> items = fetchItems();
 * String filePath = "path/to/export.csv";
 * CSVUtils.exportToCSV(filePath, items);
 *
 * // Importing items from CSV
 * Reader reader = new FileReader("path/to/import.csv");
 * List<Item> importedItems = CSVUtils.importFromCSV(reader);
 * ```
 *
 * Notes:
 * - The exported CSV file includes a header row with column names.
 * - Ensure that the CSV file format matches the expected structure for successful imports.
 *
 * @author Dennis Ward
 * @version 1.0
 * @since 01/20/2025
 */
package com.CS360.stocksense.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.CS360.stocksense.models.Item;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    /**
     * Exports a list of items to a CSV file.
     * TODO I used AI to do this
     * @param fileName The file path where the CSV file will be created.
     * @param items    The list of Item objects to export.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static void exportToCSV(Context context, String fileName, List<Item> items) throws IOException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty.");
        }

        OutputStream outputStream = null;
        Uri fileUri = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                fileUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (fileUri == null) {
                    throw new IOException("Failed to create new MediaStore entry.");
                }

                outputStream = contentResolver.openOutputStream(fileUri);
            } else {
                // For older Android versions
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                outputStream = new FileOutputStream(file);
            }

            if (outputStream == null) {
                throw new IOException("Failed to open output stream.");
            }

            CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));

            // Write CSV Header
            String[] header = {"ID", "Item Name", "Quantity", "Location", "Alert Level", "Organization Name", "Database ID", "Database Name"};
            writer.writeNext(header);

            // Write Item Data
            for (Item item : items) {
                String[] data = {
                        item.getItemId(),
                        item.getItemName(),
                        String.valueOf(item.getQuantity()),
                        item.getLocation(),
                        String.valueOf(item.getAlertLevel()),
                        item.getOrganizationId(),
                        item.getDatabaseId(),
                        item.getDatabaseName()
                };
                writer.writeNext(data);
            }

            writer.close();

        } catch (Exception e) {
            throw new IOException("Failed to export items to CSV: " + e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    /**
     * Imports a list of items from a CSV file.
     *
     * @param reader The Reader object to read the CSV file.
     * @return A list of Item objects read from the CSV file.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws IllegalArgumentException If the reader is null.
     */
    public static List<Item> importFromCSV(Reader reader) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null.");
        }

        List<Item> items = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] nextLine;
            boolean isFirstLine = true;

            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header row
                    continue;
                }

                if (nextLine.length < 5) {
                    throw new IOException("Invalid CSV format. Each row must have at least 5 columns.");
                }

                try {
                    Item item = new Item();
                    item.setItemId(nextLine[0]); // Item ID
                    item.setItemName(nextLine[1]); // Item Name
                    item.setQuantity(Integer.parseInt(nextLine[2])); // Quantity
                    item.setLocation(nextLine[3]); // Location
                    item.setAlertLevel(Integer.parseInt(nextLine[4])); // Alert Level

                    items.add(item);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid number format in CSV file: " + e.getMessage(), e);
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error validating CSV file: " + e.getMessage(), e);
        }
        return items;
    }
}
