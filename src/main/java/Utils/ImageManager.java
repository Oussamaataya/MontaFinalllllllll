package Utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Classe utilitaire pour gérer les images dans l'application
 */
public class ImageManager {

    // Constantes pour les chemins d'images
    private static final String PLACEHOLDER_IMAGE = "/img/placeholder.png";
    private static final String PRODUCTS_DIR = "images/products";
    private static final String PRODUCTS_PATH_PREFIX = "file:./images/products/";

    /**
     * Charge une image à partir d'un chemin et la retourne
     * @param imagePath Chemin de l'image
     * @return Image chargée ou image par défaut en cas d'erreur
     */
    public static Image loadImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return loadDefaultImage();
            }

            // URL externe
            if (imagePath.startsWith("http")) {
                return new Image(imagePath);
            }

            // Chemin file:
            if (imagePath.startsWith("file:")) {
                try {
                    // Gérer spécifiquement les chemins file:./ qui sont relatifs au répertoire de travail
                    if (imagePath.startsWith("file:./")) {
                        // Convertir en chemin absolu
                        String localPath = imagePath.replace("file:./", "");
                        File file = new File(localPath);

                        if (file.exists()) {
                            // Utiliser l'URI du fichier pour créer l'image
                            return new Image(file.toURI().toString());
                        } else {
                            System.out.println("Fichier non trouvé: " + localPath);
                        }
                    } else {
                        // Essayer de charger directement
                        return new Image(imagePath);
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                }
            }

            // Ressource interne
            if (imagePath.startsWith("/")) {
                try {
                    java.io.InputStream is = ImageManager.class.getResourceAsStream(imagePath);
                    if (is != null) {
                        return new Image(is);
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de la ressource: " + e.getMessage());
                }
            }

            // Essayer comme chemin local
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    return new Image(file.toURI().toString());
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du fichier local: " + e.getMessage());
            }

            // Si tout échoue, retourner l'image par défaut
            return loadDefaultImage();
        } catch (Exception e) {
            System.out.println("Erreur générale lors du chargement de l'image: " + e.getMessage());
            return loadDefaultImage();
        }
    }

    /**
     * Charge l'image par défaut
     * @return Image par défaut
     */
    public static Image loadDefaultImage() {
        try {
            // Essayer de charger l'image par défaut depuis les ressources
            java.io.InputStream is = ImageManager.class.getResourceAsStream(PLACEHOLDER_IMAGE);
            if (is != null) {
                return new Image(is);
            }

            // Si l'image par défaut n'est pas trouvée, essayer de créer une image depuis une donnée minimale
            // Créer une image 1x1 pixel transparente
            byte[] imageData = new byte[] {
                0, 0, 0, 0  // RGBA: transparent
            };
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageData);
            return new Image(bis);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());

            // En dernier recours, essayer de créer une image vide avec le constructeur le plus simple
            try {
                return new Image("");
            } catch (Exception e2) {
                // Si tout échoue, retourner null et gérer ce cas dans le code appelant
                System.out.println("Impossible de créer une image par défaut: " + e2.getMessage());
                return null;
            }
        }
    }

    /**
     * Configure une ImageView avec une image à partir d'un chemin
     * @param imageView ImageView à configurer
     * @param imagePath Chemin de l'image
     * @param width Largeur souhaitée
     * @param height Hauteur souhaitée
     */
    public static void setupImageView(ImageView imageView, String imagePath, double width, double height) {
        Image image = loadImage(imagePath);
        imageView.setImage(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
    }

    /**
     * Traite une image pour l'enregistrer dans le dossier des produits
     * @param imagePath Chemin de l'image source
     * @return Chemin de l'image traitée
     */
    public static String processProductImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return PLACEHOLDER_IMAGE;
            }

            // Si c'est déjà une URL externe ou une ressource interne, la retourner telle quelle
            if (imagePath.startsWith("http") || imagePath.startsWith("/")) {
                return imagePath;
            }

            // Si c'est déjà dans le dossier des produits, la retourner telle quelle
            if (imagePath.startsWith(PRODUCTS_PATH_PREFIX)) {
                return imagePath;
            }

            // Convertir le chemin file: en chemin local si nécessaire
            String localPath = imagePath;
            if (imagePath.startsWith("file:")) {
                try {
                    if (imagePath.startsWith("file:///")) {
                        localPath = imagePath.substring(8);
                    } else if (imagePath.startsWith("file:/")) {
                        localPath = imagePath.substring(6);
                    } else if (imagePath.startsWith("file:")) {
                        localPath = imagePath.substring(5);
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors de la conversion du chemin file:: " + e.getMessage());
                    return PLACEHOLDER_IMAGE;
                }
            }

            // Vérifier que le fichier source existe
            File sourceFile = new File(localPath);
            if (!sourceFile.exists()) {
                System.out.println("Le fichier source n'existe pas: " + localPath);
                return PLACEHOLDER_IMAGE;
            }

            // S'assurer que le dossier de destination existe
            Path targetDir = Paths.get(PRODUCTS_DIR);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Créer un nom unique pour le fichier
            String fileName = "product_" + System.currentTimeMillis() + "_" + sourceFile.getName();
            Path targetPath = targetDir.resolve(fileName);

            // Copier le fichier
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Vérifier que le fichier a bien été copié
            if (Files.exists(targetPath)) {
                // Retourner le chemin avec le préfixe file: pour l'affichage
                return PRODUCTS_PATH_PREFIX + fileName;
            } else {
                System.out.println("Erreur: Le fichier cible n'existe pas après la copie");
                return PLACEHOLDER_IMAGE;
            }
        } catch (IOException e) {
            System.out.println("Erreur IO lors du traitement de l'image: " + e.getMessage());
            return PLACEHOLDER_IMAGE;
        } catch (Exception e) {
            System.out.println("Erreur générale lors du traitement de l'image: " + e.getMessage());
            return PLACEHOLDER_IMAGE;
        }
    }
}
