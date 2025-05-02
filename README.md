# Transaction Ledger CLI Application

This is a simple **Java-based command-line application** for managing a personal or small business transaction ledger. It allows users to **record deposits and payments**, **view transactions**, and **generate financial reports** through an intuitive text-based menu system.

---

## 🧰 Features

- Add deposits and payments (with optional backdating)
- View all transactions or filter by type (deposit/payment)
- Generate financial reports:
  - Month-to-Date
  - Previous Month
  - Year-to-Date
  - Previous Year
  - Search by Vendor
  - Custom search with multiple filters
- Persist transactions to a `.csv` file
- Load and display transactions from a `.csv` file on startup

---
🧭 Application Flow
🏠 Home Menu
Users can choose to make deposits, record payments, view the ledger, or quit.

![image](https://github.com/user-attachments/assets/8397434e-b0d4-4979-9e14-da1c7cd1857d)

➕ Add Deposit
Add a deposit using the current time or a custom (backdated) one.

![image](https://github.com/user-attachments/assets/9334271e-51f7-4797-93b7-cdc76fbcd604)

➖ Make Payment
Record a payment with similar options as deposits.

![image](https://github.com/user-attachments/assets/203c58a2-b3f8-4afc-9510-f497de50de08)

📒 Ledger Menu
View all transactions, deposits, payments, or access the reports menu.

![image](https://github.com/user-attachments/assets/6a6172c0-4fce-4491-93b9-071fd387745a)

📊 Reports Menu
Generate various reports to analyze financial activity.

![image](https://github.com/user-attachments/assets/2af75e5a-173a-4fe6-9eae-7a6285c33203)

---

## ❗ Interesting Code

The piece of code I think is most interesting is my custom search report. It allows users to pick and choose what property to filter the transactions list by, with default values set when no input is given.
```
public static void customSearch(ArrayList<Transaction> transactions) {
        String startDate;
        String endDate;

        // UI divider and instructions
        MyUtils.printDivider(50);
        System.out.println("Choose how you'd like to filter our selection. Leave option blank to not filter by that option.");

        // Get start date filter (optional)
        while (true) {
            try {
                startDate = MyUtils.askQuestionGetString("Enter the earliest date you'd like to filter(yyyy-MM-dd): ").trim();
                if (!startDate.isEmpty()) {
                    startDate = LocalDate.parse(startDate, DATE_FORMAT).format(DATE_FORMAT);
                } else {
                    startDate = LocalDate.parse("1900-01-01", DATE_FORMAT).format(DATE_FORMAT); // Default earliest date
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }

        // Get end date filter (optional)
        while (true) {
            try {
                endDate = MyUtils.askQuestionGetString("Enter the latest date you'd like to filter(yyyy-MM-dd): ").trim();
                if (!endDate.isEmpty()) {
                    endDate = LocalDate.parse(endDate, DATE_FORMAT).format(DATE_FORMAT);
                } else {
                    endDate = LocalDate.now().format(DATE_FORMAT); // Default latest date
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }

        // Get optional description and vendor filters
        String description = MyUtils.askQuestionGetString("Description: ").toLowerCase().trim();
        String vendor = MyUtils.askQuestionGetString("Vendor: ").toLowerCase().trim();

        // Get optional minimum amount filter
        double minAmount;
        try {
            minAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Minimum Amount: ").trim()));
        } catch (Exception e) {
            minAmount = 0d;
        }

        // Get optional maximum amount filter
        Double maxAmount;
        try {
            maxAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Maximum Amount: ").trim()));
        } catch (Exception e) {
            maxAmount = null;
        }

        // Filter transactions based on all input criteria
        ArrayList<Transaction> filteredTransactions = filterTransactions(transactions, startDate, endDate, description, vendor, minAmount, maxAmount);

        // Display results or indicate no matches found
        if (filteredTransactions.isEmpty()) {
            System.out.println("No items match search.");
        }
        showAll(filteredTransactions);
    }

    public static ArrayList<Transaction> filterTransactions(ArrayList<Transaction> transactions, String startDate, String endDate, String description, String vendor, double minAmount, Double maxAmount) {
        // Return a filtered list based on input criteria using Stream API
        return transactions.stream()
                // Filter by date range
                .filter(transaction -> LocalDate.parse(transaction.getDate(), DATE_FORMAT).isAfter(LocalDate.parse(startDate, DATE_FORMAT)))
                .filter(transaction -> LocalDate.parse(transaction.getDate(), DATE_FORMAT).isBefore(LocalDate.parse(endDate, DATE_FORMAT)))
                // Filter by description if provided
                .filter(transaction -> description == null || description.isEmpty() || transaction.getDescription().toLowerCase().contains(description.toLowerCase()))
                // Filter by vendor if provided
                .filter(transaction -> vendor == null || vendor.isEmpty() || transaction.getVendor().toLowerCase().contains(vendor.toLowerCase()))
                // Filter by minimum amount
                .filter(transaction -> Math.abs(transaction.getAmount()) >= minAmount)
                // Filter by maximum amount if provided
                .filter(transaction -> maxAmount == null || Math.abs(transaction.getAmount()) <= maxAmount)
                // Collect results into a new list
                .collect(Collectors.toCollection(ArrayList::new));
    }
```
