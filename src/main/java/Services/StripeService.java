package Services;


import Entities.Commande;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StripeService {

    // Remplacer par votre clé secrète Stripe
    private static final String STRIPE_SECRET_KEY = "sk_test_51Axxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    
    // Initialiser l'API Stripe avec la clé secrète
    static {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }
    
    /**
     * Crée une intention de paiement dans Stripe
     * 
     * @param amount Montant en euros (sera converti en centimes)
     * @param currency Devise du paiement (par défaut EUR)
     * @param description Description du paiement
     * @param customerEmail Email du client
     * @return L'objet PaymentIntent contenant client_secret, etc.
     * @throws StripeException Si une erreur Stripe se produit
     */
    public PaymentIntent createPaymentIntent(double amount, String currency, String description, String customerEmail) throws StripeException {
        // Convertir le montant en centimes (Stripe utilise la plus petite unité de devise)
        long amountInCents = Math.round(amount * 100);
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setDescription(description)
                .setReceiptEmail(customerEmail)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
                
        return PaymentIntent.create(params);
    }
    
    /**
     * Confirme un paiement après que le client a saisi ses informations de carte
     * 
     * @param paymentIntentId ID de l'intention de paiement
     * @param paymentMethodId ID de la méthode de paiement
     * @return PaymentIntent mis à jour
     * @throws StripeException Si une erreur Stripe se produit
     */
    public PaymentIntent confirmPayment(String paymentIntentId, String paymentMethodId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", paymentMethodId);
        
        return intent.confirm(params);
    }
    
    /**
     * Génère le JSON nécessaire pour initialiser Stripe Elements dans le frontend
     * 
     * @param commande La commande à payer
     * @param customerEmail Email du client
     * @return String JSON contenant les données pour Stripe Elements
     * @throws StripeException Si une erreur Stripe se produit
     */
    public String generateStripeConfigJSON(Commande commande, String customerEmail) throws StripeException {
        String description = "Commande #" + commande.getId();
        
        PaymentIntent paymentIntent = createPaymentIntent(
                commande.getPrixtotal(), 
                "eur", 
                description, 
                customerEmail
        );
        
        JSONObject config = new JSONObject();
        config.put("publishableKey", "pk_test_51Axxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"); // Remplacer par votre clé publique
        config.put("clientSecret", paymentIntent.getClientSecret());
        config.put("amount", commande.getPrixtotal());
        config.put("currency", "EUR");
        
        return config.toString();
    }
    
    /**
     * Vérifie si un paiement a réussi
     * 
     * @param paymentIntentId ID de l'intention de paiement
     * @return true si le paiement a réussi, false sinon
     * @throws StripeException Si une erreur Stripe se produit
     */
    public boolean isPaymentSuccessful(String paymentIntentId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        return "succeeded".equals(intent.getStatus());
    }
} 