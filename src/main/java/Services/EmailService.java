package Services;

import Entities.Commande;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailService {
    private static final String FROM_EMAIL = "testp3253@gmail.com"; // Replace with your actual email
    private static final String PASSWORD = "fqos fwho fpjp tpzv"; // Use app password for Gmail

    public boolean sendEmail(String recipientEmail, String subject, String messageContent) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageContent);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends an email notification about an order status change
     * 
     * @param recipientEmail The email address of the recipient
     * @param orderStatus The new status of the order
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendOrderStatusNotification(String recipientEmail, String orderStatus) {
        String subject = "Mise à jour de votre commande Ecolink";
        StringBuilder messageBuilder = new StringBuilder();
        
        messageBuilder.append("Bonjour,\n\n");
        messageBuilder.append("Votre commande est maintenant ").append(orderStatus).append(".\n\n");
        
        if (orderStatus.equals("En Livraison")) {
            messageBuilder.append("Bonne nouvelle! Votre commande est en route vers votre adresse de livraison.\n");
            messageBuilder.append("Vous devriez la recevoir très prochainement.\n\n");
            messageBuilder.append("Pour toute question concernant votre livraison, n'hésitez pas à nous contacter.\n\n");
        }
        
        messageBuilder.append("Merci d'avoir choisi Ecolink.\n\n");
        messageBuilder.append("L'équipe Ecolink");
        
        return sendEmail(recipientEmail, subject, messageBuilder.toString());
    }
    
    /**
     * Sends an email notification about an order status change with order details
     * 
     * @param commande The order with updated status
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendOrderStatusNotification(Commande commande) {
        String subject = "Mise à jour de votre commande Ecolink #" + commande.getId();
        StringBuilder messageBuilder = new StringBuilder();
        
        messageBuilder.append("Bonjour ").append(commande.getClient_name()).append(" ").append(commande.getClient_family_name()).append(",\n\n");
        messageBuilder.append("Votre commande #").append(commande.getId()).append(" est maintenant ").append(commande.getEtatCommande()).append(".\n\n");
        
        if (commande.getEtatCommande().equals("En Livraison")) {
            messageBuilder.append("Bonne nouvelle! Votre commande est en route vers l'adresse suivante:\n");
            messageBuilder.append(commande.getClient_adresse()).append("\n\n");
            messageBuilder.append("Vous devriez la recevoir très prochainement.\n\n");
            messageBuilder.append("Détails de la commande:\n");
            messageBuilder.append("- Montant: ").append(commande.getPrixtotal()).append(" D\n");
            messageBuilder.append("- Méthode de paiement: ").append(commande.getMethodePaiement()).append("\n\n");
            messageBuilder.append("Pour toute question concernant votre livraison, n'hésitez pas à nous contacter.\n\n");
        }
        
        messageBuilder.append("Merci d'avoir choisi Ecolink.\n\n");
        messageBuilder.append("L'équipe Ecolink");
        
        return sendEmail(commande.getClient_email(), subject, messageBuilder.toString());
    }
    
    /**
     * Sends an order confirmation email for a newly created order
     * @param commande The order that was created
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendOrderConfirmationEmail(Commande commande) {
        String subject = "Confirmation de votre commande Ecolink #" + commande.getId();
        
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Bonjour ").append(commande.getClient_name()).append(" ").append(commande.getClient_family_name()).append(",\n\n");
        messageBuilder.append("Nous vous confirmons que votre commande #").append(commande.getId()).append(" a été créée avec succès.\n\n");
        messageBuilder.append("Détails de la commande :\n");
        messageBuilder.append("- Date : ").append(commande.getDate()).append("\n");
        messageBuilder.append("- Montant total : ").append(commande.getPrixtotal()).append(" €\n");
        messageBuilder.append("- Méthode de paiement : ").append(commande.getMethodePaiement()).append("\n");
        
        // Add user ID information if available
        if (commande.getUserId() > 0) {
            messageBuilder.append("- Commande associée au compte utilisateur #").append(commande.getUserId()).append("\n");
        }
        
        messageBuilder.append("\nNous vous informerons par email lorsque votre commande sera en cours de préparation, puis en cours de livraison.\n\n");
        messageBuilder.append("Merci d'avoir choisi Ecolink !\n\n");
        messageBuilder.append("L'équipe Ecolink");
        
        return sendEmail(commande.getClient_email(), subject, messageBuilder.toString());
    }
} 