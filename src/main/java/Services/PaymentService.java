package Services;


import Entities.Commande;
import Entities.Produit;
import Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class PaymentService {
    private final Connection cnx = MyDataBase.getInstance().getConnection();
    private final CommandeService commandeService = new CommandeService();

    /**
     * Process payment and create order
     * @param clientName Customer's first name
     * @param clientFamilyName Customer's last name
     * @param clientAddress Customer's address
     * @param clientPhone Customer's phone number
     * @param email Customer's email
     * @param paymentMethod Payment method (combined type and method)
     * @param produits Products in the cart
     * @param specialInstructions Special instructions for the order
     * @param total Total amount
     * @return The created order if successful, null otherwise
     */
    public Commande processPayment(
            String clientName,
            String clientFamilyName,
            String clientAddress,
            String clientPhone,
            String email,
            String paymentMethod,
            List<Produit> produits,
            String specialInstructions,
            float total
    ) {
        try {
            // Create order
            Commande commande = new Commande(
                    clientName,
                    clientFamilyName,
                    clientAddress,
                    clientPhone,
                    email,
                    paymentMethod,
                    "En attente",
                    LocalDateTime.now(),
                    specialInstructions,
                    total
            );

            // Save order to database
            int commandeId = commandeService.add(commande);
            if (commandeId > 0) {
                commande.setId(commandeId);
                
                // Save order items
                commandeService.addProduitToCommande(produits, commande);
                
                return commande;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Validate credit card information
     * @param cardNumber Card number
     * @param expiryDate Expiry date (MM/YY)
     * @param cvc CVC code
     * @return true if card info is valid, false otherwise
     */
    public boolean validateCardInfo(String cardNumber, String expiryDate, String cvc) {
        // Validate card number (Luhn algorithm)
        if (!isValidCardNumber(cardNumber)) {
            return false;
        }

        // Validate expiry date format (MM/YY)
        if (!isValidExpiryDate(expiryDate)) {
            return false;
        }

        // Validate CVC (3 or 4 digits)
        if (!isValidCVC(cvc)) {
            return false;
        }

        return true;
    }

    private boolean isValidCardNumber(String cardNumber) {
        // Remove spaces and dashes
        cardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Check if it's a number and has correct length
        if (!cardNumber.matches("\\d{13,19}")) {
            return false;
        }

        // Luhn algorithm
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (!expiryDate.matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
            return false;
        }

        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // Assuming 20xx

        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        return (year > currentYear) || (year == currentYear && month >= currentMonth);
    }

    private boolean isValidCVC(String cvc) {
        return cvc.matches("\\d{3,4}");
    }
}