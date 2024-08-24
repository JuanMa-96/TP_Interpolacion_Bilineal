package org.example;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static String pathIn = "/in/imagen_in.pgm";
    public static String pathOut = "image_out.pgm";
    public static void main(String[] args) throws IOException {
        // Cargamos la imagen con una ruta determinada
        int[][] inputImage = loadImage(pathIn);

        // Indicamos el tamaño de la imagen a reescalar
        int newWidth = 800;
        int newHeight = 600;

        // Llamamos a la funcion que reescala la imagen
        int[][] scaledImage = resizeImage(inputImage, newWidth, newHeight);

        // Guardamos el resultado
        saveImage(scaledImage, newWidth, newHeight, pathOut);
    }

    public static int[][] resizeImage(int[][] originalImage, int newWidth, int newHeight) {
        int originalHeight = originalImage.length;
        int originalWidth = originalImage[0].length;
        int[][] scaledImage = new int[newHeight][newWidth];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                // Calcular las coordenadas originales correspondientes
                float x = ((float) j / newWidth) * (originalWidth - 1);
                float y = ((float) i / newHeight) * (originalHeight - 1);

                // Coordenadas de los píxeles vecinos
                int x1 = (int) Math.floor(x);
                int y1 = (int) Math.floor(y);
                int x2 = Math.min(x1 + 1, originalWidth - 1);
                int y2 = Math.min(y1 + 1, originalHeight - 1);

                // Diferencias
                float dx = x - x1;
                float dy = y - y1;

                // Interpolación bilineal
                int top = (int) ((1 - dx) * originalImage[y1][x1] + dx * originalImage[y1][x2]);
                int bottom = (int) ((1 - dx) * originalImage[y2][x1] + dx * originalImage[y2][x2]);
                int interpolatedValue = (int) ((1 - dy) * top + dy * bottom);

                scaledImage[i][j] = interpolatedValue;
            }
        }

        return scaledImage;
    }


    // Metodo para cargar el mapa de escala de grises en una matriz
    private static int[][] loadImage(String path) throws IOException {
        try {
            InputStream fileInputStream = Main.class.getResourceAsStream(path);
            Scanner scan = new Scanner(fileInputStream);
            System.out.println(scan.nextLine());
            int picWidth = scan.nextInt();
            System.out.println(picWidth);
            int picHeight = scan.nextInt();
            System.out.println(picHeight);
            int maxvalue = scan.nextInt();
            System.out.println(maxvalue);

            fileInputStream.close();

            fileInputStream = Main.class.getResourceAsStream(path);
            DataInputStream dis = new DataInputStream(fileInputStream);

            // While para ignorar las lineas con simbolos raros al principio del archivo (header)
            int numnewlines = 4;
            while (numnewlines > 0) {
                char c;
                do {
                    c = (char)(dis.readUnsignedByte());
                } while (c != '\n');
                numnewlines--;
            }

            // Leemos los datos de la imagen
            int[][] data2D = new int[picHeight][picWidth];
            for (int row = 0; row < picHeight; row++) {
                for (int col = 0; col < picWidth; col++) {
                    data2D[row][col] = dis.readUnsignedByte();
                    //System.out.print(data2D[row][col] + " ");
                }
                //System.out.println();
            }
            return data2D;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static void saveImage(int[][] image, int width, int height, String fileName) {
        // Armamos el path para guardar la imagen
        String directory = "src\\main\\resources\\out";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Genero el path completo del archivo de salida
        String filePath = Paths.get(directory, fileName).toString();

        try (FileWriter writer = new FileWriter(filePath)) {
            // Cabecera del archivo PGM
            writer.write("P5\n");
            writer.write(width + " " + height + "\n");
            writer.write("255\n");

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    writer.write(image[i][j] + " ");
                }
                writer.write("\n");
            }

            System.out.println("Imagen guardada como " + filePath);

        } catch (IOException e) {
            System.err.println("Error al guardar la imagen PGM: " + e.getMessage());
        }
    }
}