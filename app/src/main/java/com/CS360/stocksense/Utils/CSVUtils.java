package com.CS360.stocksense.Utils;

import com.CS360.stocksense.models.Item;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    // Export a list of items to a CSV file
    public static void exportToCSV(String filePath, List<Item> items) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            String[] header = {"ID", "Item Name", "Quantity", "Location", "Alert Level", "Organization Name", "Database ID", "Database Name"};
            writer.writeNext(header);

            // Write item data
            for (Item item : items) {
                String[] data = {
                        item.getItem_id() != null ? item.getItem_id().toString() : "",
                        item.getItemName(),
                        String.valueOf(item.getQuantity()),
                        item.getLocation(),
                        String.valueOf(item.getAlertLevel()),
                        item.getOrganizationName(),
                        item.getDatabaseId(),
                        item.getDatabaseName()
                };
                writer.writeNext(data);
            }
        }
    }

    // Import a list of items from a CSV file
    public static List<Item> importFromCSV(Reader reader) throws IOException {
        List<Item> items = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] nextLine;
            boolean isFirstLine = true;

            while ((nextLine = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header row
                }

                Item item = new Item();
                item.setItem_id(nextLine[0]); // Set item_id from CSV // Item ID
                item.setItemName(nextLine[1]); // Item Name
                item.setQuantity(Integer.parseInt(nextLine[2])); // Quantity
                item.setLocation(nextLine[3]); // Location
                item.setAlertLevel(Integer.parseInt(nextLine[4])); // Alert Level

                items.add(item);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return items;
    }


}
