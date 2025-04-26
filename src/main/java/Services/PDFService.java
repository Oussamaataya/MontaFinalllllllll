package Services;


import Entities.Commande;
import Entities.Produit;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PDFService {
    
    /**
     * Generate a PDF document for an order
     * @param commande The order to generate the PDF for
     * @return The path to the generated PDF file
     */
    public String generateOrderPDF(Commande commande) {
        Document document = new Document(PageSize.A4);
        String fileName = "commande_" + commande.getId() + ".pdf";
        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + File.separator + "Downloads";
        String facturesPath = downloadsPath + File.separator + "Factures";
        String filePath = facturesPath + File.separator + fileName;
        
        try {
            // Create factures directory if it doesn't exist
            File directory = new File(facturesPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add logo if available
            try {
                Image logo = Image.getInstance(getClass().getResource("/com/example/ecolink/images/logo.png"));
                logo.scaleToFit(150, 150);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("Logo not found or couldn't be loaded: " + e.getMessage());
            }
            
            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph title = new Paragraph("Détails de la Commande #" + commande.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Add date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Paragraph dateParagraph = new Paragraph("Date: " + commande.getDate().format(formatter), dateFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.setSpacingAfter(20);
            document.add(dateParagraph);
            
            // Add client information
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
            Paragraph clientSection = new Paragraph("Information Client", sectionFont);
            clientSection.setSpacingAfter(10);
            document.add(clientSection);
            
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            document.add(new Paragraph("Nom: " + commande.getClient_name() + " " + commande.getClient_family_name(), normalFont));
            document.add(new Paragraph("Adresse: " + commande.getClient_adresse(), normalFont));
            document.add(new Paragraph("Téléphone: " + commande.getClient_phone(), normalFont));
            document.add(new Paragraph("Email: " + commande.getClient_email(), normalFont));
            
            // Add order details
            Paragraph orderSection = new Paragraph("Détails de la Commande", sectionFont);
            orderSection.setSpacingBefore(20);
            orderSection.setSpacingAfter(10);
            document.add(orderSection);
            
            document.add(new Paragraph("État: " + commande.getEtatCommande(), normalFont));
            document.add(new Paragraph("Méthode de paiement: " + commande.getMethodePaiement(), normalFont));
            if (commande.getInstructionSpeciale() != null && !commande.getInstructionSpeciale().isEmpty()) {
                document.add(new Paragraph("Instructions spéciales: " + commande.getInstructionSpeciale(), normalFont));
            }
            
            // Add products table
            Paragraph productsSection = new Paragraph("Produits Commandés", sectionFont);
            productsSection.setSpacingBefore(20);
            productsSection.setSpacingAfter(10);
            document.add(productsSection);
            
            if (commande.getProduits() != null && !commande.getProduits().isEmpty()) {
                PdfPTable table = new PdfPTable(4); // 4 columns
                table.setWidthPercentage(100);
                
                // Set column widths
                float[] columnWidths = {2f, 5f, 1f, 2f};
                table.setWidths(columnWidths);
                
                // Add table headers
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                
                PdfPCell headerCell;
                
                headerCell = new PdfPCell(new Phrase("ID", headerFont));
                headerCell.setBackgroundColor(new BaseColor(66, 139, 202));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(8);
                table.addCell(headerCell);
                
                headerCell = new PdfPCell(new Phrase("Produit", headerFont));
                headerCell.setBackgroundColor(new BaseColor(66, 139, 202));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(8);
                table.addCell(headerCell);
                
                headerCell = new PdfPCell(new Phrase("Qté", headerFont));
                headerCell.setBackgroundColor(new BaseColor(66, 139, 202));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(8);
                table.addCell(headerCell);
                
                headerCell = new PdfPCell(new Phrase("Prix", headerFont));
                headerCell.setBackgroundColor(new BaseColor(66, 139, 202));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(8);
                table.addCell(headerCell);
                
                // Add table rows
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
                for (Produit produit : commande.getProduits()) {
                    PdfPCell cell;
                    
                    cell = new PdfPCell(new Phrase(String.valueOf(produit.getId()), cellFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase(produit.getNom_produit(), cellFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase(String.valueOf(produit.getQuantity()), cellFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase(String.format("%.2f", produit.getPrix()) + " €", cellFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(5);
                    table.addCell(cell);
                }
                
                document.add(table);
            } else {
                document.add(new Paragraph("Aucun produit dans cette commande", normalFont));
            }
            
            // Add total
            Paragraph totalSection = new Paragraph("Total: " + String.format("%.2f", commande.getPrixtotal()) + " €", sectionFont);
            totalSection.setAlignment(Element.ALIGN_RIGHT);
            totalSection.setSpacingBefore(20);
            document.add(totalSection);
            
            // Add footer
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Document généré le " + 
                                            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + 
                                            " - Ecolink", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            writer.close();
            
            System.out.println("PDF Order #" + commande.getId() + " generated successfully: " + filePath);
            return filePath;
            
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            System.err.println("Error generating PDF: " + e.getMessage());
            return null;
        }
    }
} 