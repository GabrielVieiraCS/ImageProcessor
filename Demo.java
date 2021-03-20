import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class Demo extends Component implements ActionListener {

    public static JFrame frame = new JFrame("Image Processing Demo");
    public static JPanel container = new JPanel();
    public static JPanel panel2 = new JPanel();
    public int compIndex = 0;

    // ************************************
    // List of the options(Original, Negative); correspond to the cases:
    // ************************************

    String descs[] = { "Original", "Negative", "Rescale", "Rescale RND", "Close Comp", "Addition", "Subtraction", "Multiplication", "Division", "Not", "And", "Or", "XOr", "Log FN", "Power-Law", "Look-Up RND", "BPS"};

    String descs_Comparision[] = { "Original", "Negative", "Close" };

    String editOptions[] = { "Undo", "Redo" };

    int opIndex; // option index for
    int lastOp;

    int topIndex;
    int currIndex;
    int prevIndex;

    ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();

    private BufferedImage bi, biFiltered, lab3Image; // the input image saved as bi;//
    int w, h;

    public Demo() {
        try {
            bi = ImageIO.read(
                    new File("\\C:\\Users\\gfvie\\Documents\\University\\Image Processing\\Labs\\Images\\BaboonRGB.bmp"));
            lab3Image = ImageIO.read(new File(
                        "\\C:\\Users\\gfvie\\Documents\\University\\Image Processing\\Labs\\Images\\PeppersRGB.bmp"));
            // Add Image to ArrayList
            imageList.add(bi);
            topIndex++;
            currIndex++;
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (IOException e) { // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h); // EDITED TO FIT 2
    }

    String[] getDescriptions() {
        return descs;
    }

    String[] getDescriptions_Comparisions() {
        return descs_Comparision;
    }

    String[] getEditOptions() {
        return editOptions;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = { "bmp", "gif", "jpeg", "jpg", "png" };
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { // Repaint will call this function so the image will change.
        filterImage();

        g.drawImage(biFiltered, 0, 0, null);
    }

    // ************************************
    // Convert the Buffered Image to Array
    // ************************************
    private static int[][][] convertToArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                result[x][y][0] = a;
                result[x][y][1] = r;
                result[x][y][2] = g;
                result[x][y][3] = b;
            }
        }
        return result;
    }

    // ************************************
    // Convert the Array to BufferedImage
    // ************************************
    public BufferedImage convertToBimage(int[][][] TmpArray) {

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                // set RGB value

                int p = (a << 24) | (r << 16) | (g << 8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    // ************************************
    // Example: Image Negative
    // ************************************
    public BufferedImage ImageNegative(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); // Convert the image to array

        // Image Negative Operation:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = 255 - ImageArray[x][y][1]; // r
                ImageArray[x][y][2] = 255 - ImageArray[x][y][2]; // g
                ImageArray[x][y][3] = 255 - ImageArray[x][y][3]; // b
            }
        }

        return convertToBimage(ImageArray); // Convert the array to BufferedImage
    }

    // ************************************
    // Your turn now: Add more function below
    // ************************************

    // RESCALE & SHIFT

    public BufferedImage rescaleInput(BufferedImage timg) {
        JFrame f = new JFrame();
        String inputString1 = JOptionPane.showInputDialog(f, "Input the rescale factor (0 - 2)");
        String inputString2 = JOptionPane.showInputDialog(f, "Input the shift factor");

        Float s = Float.valueOf(inputString1).floatValue();
        if (s > 2) {
            s = (float) 2.0;
        } else if (s < 0) {
            s = (float) 0.0;
        }
        int t = Integer.valueOf(inputString2);

        BufferedImage outputImage = rescaleImage(timg, s, t);
        return outputImage;
    }

    // To shift by t and rescale by s without finding the min and the max
    public BufferedImage rescaleImage(BufferedImage timg, Float s, int t) {
        // Normal Values are 1 & 255

        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray1 = convertToArray(timg); // Convert the image to array
        int[][][] ImageArray2 = ImageArray1;
        // To shift by t and rescale by s without finding the min and the max
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray2[x][y][1] = (int) (s * (ImageArray1[x][y][1] + t)); // r
                ImageArray2[x][y][2] = (int) (s * (ImageArray1[x][y][2] + t)); // g
                ImageArray2[x][y][3] = (int) (s * (ImageArray1[x][y][3] + t)); // b
                if (ImageArray2[x][y][1] < 0) {
                    ImageArray2[x][y][1] = 0;
                }
                if (ImageArray2[x][y][2] < 0) {
                    ImageArray2[x][y][2] = 0;
                }
                if (ImageArray2[x][y][3] < 0) {
                    ImageArray2[x][y][3] = 0;
                }
                if (ImageArray2[x][y][1] > 255) {
                    ImageArray2[x][y][1] = 255;
                }
                if (ImageArray2[x][y][2] > 255) {
                    ImageArray2[x][y][2] = 255;
                }
                if (ImageArray2[x][y][3] > 255) {
                    ImageArray2[x][y][3] = 255;
                }
            }
        }
        return convertToBimage(ImageArray2); // Convert the array to BufferedImage
    }

    

    // RESCALE & SHIFT RANDOM

    // Re-Scale and Shifting
    public BufferedImage rndShiftRescaleImage(BufferedImage timg) {
        int[][][] imageArray = convertToArray(timg);
        Random rnd = new Random();
        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                imageArray[x][y][1] = (int) imageArray[x][y][1] + rnd.nextInt(255); // r
                imageArray[x][y][2] = (int) imageArray[x][y][2] + rnd.nextInt(255); // g
                imageArray[x][y][3] = (int) imageArray[x][y][3] + rnd.nextInt(255); // b
            }
        }
        int s = 255;
        int t = 1;

        int rmin = s * (imageArray[0][0][1] + t);
        int rmax = rmin;
        int gmin = s * (imageArray[0][0][2] + t);
        int gmax = gmin;
        int bmin = s * (imageArray[0][0][3] + t);
        int bmax = bmin;
        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                imageArray[x][y][1] = s * (imageArray[x][y][1] + t); // r
                imageArray[x][y][2] = s * (imageArray[x][y][2] + t); // g
                imageArray[x][y][3] = s * (imageArray[x][y][3] + t); // b
                if (rmin > imageArray[x][y][1]) {
                    rmin = imageArray[x][y][1];
                }
                if (gmin > imageArray[x][y][2]) {
                    gmin = imageArray[x][y][2];
                }
                if (bmin > imageArray[x][y][3]) {
                    bmin = imageArray[x][y][3];
                }
                if (rmax < imageArray[x][y][1]) {
                    rmax = imageArray[x][y][1];
                }
                if (gmax < imageArray[x][y][2]) {
                    gmax = imageArray[x][y][2];
                }
                if (bmax < imageArray[x][y][3]) {
                    bmax = imageArray[x][y][3];
                }
            }
        }
        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                imageArray[x][y][1] = 255 * (imageArray[x][y][1] - rmin) / (rmax - rmin);
                imageArray[x][y][2] = 255 * (imageArray[x][y][2] - gmin) / (gmax - gmin);
                imageArray[x][y][3] = 255 * (imageArray[x][y][3] - bmin) / (bmax - bmin);
            }
        }
        return convertToBimage(imageArray);
    }


    /*
    ----------------------------------- LAB 3 EXERCISES ---------------------------------------------------------------------------------------------
    */

    // ADDITION
    public BufferedImage imageAddtion(BufferedImage timg, BufferedImage timg2) {
        int[][][] imageArray = convertToArray(timg);
        int[][][] imageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[imageArray.length][imageArray[0].length][4]; // Creates Array to store final output of addition operation

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                outImg[x][y][1] = (imageArray[x][y][1] + imageArray2[x][y][1]);
                outImg[x][y][2] = (imageArray[x][y][2] + imageArray2[x][y][2]);
                outImg[x][y][3] = (imageArray[x][y][3] + imageArray2[x][y][3]);
            }
        }

        return rescaleImage(convertToBimage(outImg), (float)255.0, 1);
    }

    // SUBTRACTION
    public BufferedImage imageSubtraction(BufferedImage timg, BufferedImage timg2) {
        int[][][] imageArray = convertToArray(timg);
        int[][][] imageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[imageArray.length][imageArray[0].length][4]; // Creates Array to store final output of subtraction operation

        for (int y = 0; y < timg.getHeight(); y++) {
            for (int x = 0; x < timg.getWidth(); x++) {
                outImg[x][y][1] = (imageArray[x][y][1] - imageArray2[x][y][1]);
                outImg[x][y][2] = (imageArray[x][y][2] - imageArray2[x][y][2]);
                outImg[x][y][3] = (imageArray[x][y][3] - imageArray2[x][y][3]);
            }
        }

        return rescaleImage(convertToBimage(outImg), (float)255.0, 1);
    }

    //MULTIPLICATION
    public BufferedImage imageMultiplication(BufferedImage timg, BufferedImage timg2){
        int[][][] imageArray = convertToArray(timg);
        int[][][] imageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[imageArray.length][imageArray[0].length][4];

        for(int y=0; y < timg.getHeight(); y++){
            for(int x=0; x < timg.getWidth(); x++){
                for(int p = 1; p < 4; p++){
                    outImg[x][y][p] = imageArray[x][y][p] * (imageArray2[x][y][p]+1);
                }
            }
        }

        return rescaleImage(convertToBimage(outImg), (float)255.0, 1);
    }

    //DIVISION
    public BufferedImage imageDivision(BufferedImage timg, BufferedImage timg2){
        int[][][] imageArray = convertToArray(timg);
        int[][][] imageArray2 = convertToArray(timg2);
        int[][][] outImg = new int[imageArray.length][imageArray[0].length][4];

        for(int y=0; y < timg.getHeight(); y++){
            for(int x=0; x < timg.getWidth(); x++){
                for(int p = 1; p < 4; p++){
                    outImg[x][y][p] = imageArray[x][y][p] / (imageArray2[x][y][p]+1);
                }
            }
        }

        return rescaleImage(convertToBimage(outImg), (float)255.0, 1);
    }
   
    //NOT
    public BufferedImage imageNot(BufferedImage timg){
        int[][][] imageArray = convertToArray(timg);

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                int r = imageArray[x][y][1];
                int g = imageArray[x][y][2];
                int b = imageArray[x][y][3];
                imageArray[x][y][1] = (~r)& 0xFF; 
                imageArray[x][y][2] = (~g)& 0xFF; 
                imageArray[x][y][3] = (~b)& 0xFF; 
            }
        }
        return convertToBimage(imageArray);
    }

    //AND
    public BufferedImage imageAnd(BufferedImage timg, BufferedImage timg2){
        int[][][] imageArray = convertToArray(timg);
        int[][][] imageArray2 = convertToArray(timg2);

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                imageArray[x][y][1] = (imageArray[x][y][1] & imageArray2[x][y][1])& 0xFF; 
                imageArray[x][y][2] = (imageArray[x][y][2] & imageArray2[x][y][2])& 0xFF;
                imageArray[x][y][3] = (imageArray[x][y][3] & imageArray2[x][y][3])& 0xFF;
            }
        }
        return convertToBimage(imageArray);
    }

    //OR
    public BufferedImage imageOr(BufferedImage timg, BufferedImage timg2){
        int[][][] image = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                image[x][y][1] = (image[x][y][1] | image2[x][y][1])& 0xFF; 
                image[x][y][2] = (image[x][y][2] | image2[x][y][2])& 0xFF;
                image[x][y][3] = (image[x][y][3] | image2[x][y][3])& 0xFF;
            }
        }
        return convertToBimage(image);
    }

    //XOR
    public BufferedImage XOr(BufferedImage timg, BufferedImage timg2){
        int[][][] image = convertToArray(timg);
        int[][][] image2 = convertToArray(timg2);

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                image[x][y][1] = (image[x][y][1] ^ image2[x][y][1])& 0xFF; 
                image[x][y][2] = (image[x][y][2] ^ image2[x][y][2])& 0xFF;
                image[x][y][3] = (image[x][y][3] ^ image2[x][y][3])& 0xFF;
            }
        }
        return convertToBimage(image);
    }

    // TO DO
    public BufferedImage ROI(BufferedImage timg, BufferedImage msk){
        return imageOr(timg, msk);
    }




    /*
    ----------------------------------- LAB 4 EXERCISES ---------------------------------------------------------------------------------------------
    */

    //LOGARITHMIC FUNCTION
    public BufferedImage logFunction(BufferedImage timg) {
        int [][][] imageArray = convertToArray(timg);

        //To apply logarithmic function s = c log(1+r) to images
        double c = 255/(Math.log(256));
        for(int y=0; y < timg.getHeight(); y++){
            for(int x=0; x < timg.getWidth(); x++){
                for(int p = 1; p < 4; p++){
                    imageArray[x][y][p] = (int)(c * Math.log(imageArray[x][y][p]));
                }
            }
        }

        return convertToBimage(imageArray);
    }

    //POWER-LAW FUNCTION
    public BufferedImage powerInput(BufferedImage timg) {
        JFrame f = new JFrame();
        String inputString1 = JOptionPane.showInputDialog(f, "Input the power value (0.01 - 25)");
        double p = Float.valueOf(inputString1).floatValue();
        if (p > 25) {
            p = (double) 25.0;
        } else if (p < 0.01) {
            p = (double) 0.01;
        }

        BufferedImage outputImage = powFunction(timg, p);
        return outputImage;
    }

    public BufferedImage powFunction(BufferedImage timg, double p) {
        int [][][] imageArray = convertToArray(timg);

        //To apply power law s = c r^p to images with different powers from 0.01 to 25
        double c = Math.pow(255, 1-p);
        
        //Image is 255, so we use Y = 2.5
        double Y = 2.5;

        for(int y=0; y < timg.getHeight(); y++){
            for(int x=0; x < timg.getWidth(); x++){
                for(int t = 1; t < 4; t++){
                    imageArray[x][y][t] = (int)(c * Math.pow(imageArray[x][y][t],Y)); 
                }
            }
        }

        return convertToBimage(imageArray);
    }


    //RANDOM LOOK-UP TABLE
    public BufferedImage randomLookUpFunction(BufferedImage timg){
        int[][][] imageArray = convertToArray(timg);

        int[] lut = new int[256];
        Random rnd = new Random();

        for(int i = 0; i < lut.length; i++){
            lut[i] = rnd.nextInt(256);
        }

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                imageArray[x][y][1] = lut[imageArray[x][y][1]]; 
                imageArray[x][y][2] = lut[imageArray[x][y][2]];
                imageArray[x][y][3] = lut[imageArray[x][y][3]];
            }
        }
        return convertToBimage(imageArray);
    }


    //BIT-PLANE SLICING
    public BufferedImage bitPlaneSliceFunction(BufferedImage timg){
        int[][][] imageArray = convertToArray(timg);
        int bit = 3;

        for(int y = 0; y < timg.getHeight(); y++){
            for(int x = 0; x < timg.getWidth(); x++){
                imageArray[x][y][1] = ((imageArray[x][y][1] >> bit) &1)* 255; 
                imageArray[x][y][2] = ((imageArray[x][y][2] >> bit) &1)* 255; 
                imageArray[x][y][3] = ((imageArray[x][y][3] >> bit) &1)* 255; 
            }
        }

        return convertToBimage(imageArray);
    }



    /*
    ----------------------------------- LAB 5 EXERCISES ---------------------------------------------------------------------------------------------
    */

    //EX1) FINDING HISTOGRAM
    public void findHistogram(BufferedImage timg) {
        int [][][] imageArray = convertToArray(timg);

        for(int k=0; k<256; k++){ // Initialisation
            HistgramR[k] = 0;
            HistgramG[k] = 0;
            HistgramB[k] = 0;
        }

        for(int y=0; y<timg.height; y++){ // bin histograms
            for(int x=0; x<timg.width; x++){
                r = ImageArray[x][y][1]; //r
                g = ImageArray[x][y][2]; //g
                b = ImageArray[x][y][3]; //b
                HistgramR[r]++;
                HistgramG[g]++;
                HistgramB[b]++;
            }
        }

        for(int k=0; k<256; k++){ // Normalisation
        nHistgramR[k] = HistgramR[k]/timg.height/timg.width; // r
        nHistgramG[k] = HistgramG[k]/timg.height/timg.width; // g
        nHistgramB[k] = HistgramB[k]/timg.height/timg.width; // b
        }
    }
    

    /*
    ----------------------------------- LAB 6 EXERCISES ---------------------------------------------------------------------------------------------
    */

    //Array to store all required 3x3 masks
    int[][][] maskArray = {

    };

    public BufferedImage imageConvolution(BufferedImage timg) {
        int [][][] imageArray = convertToArray(timg);

        return convertToBimage(imageArray);
    }


    // ************************************
    // You need to register your function here
    // ************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0:
                biFiltered = bi; /* original */
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                System.out.println(imageList.size()); // DEBUG
                return;
            case 1:
                biFiltered = ImageNegative(bi); /* Image Negative */
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                System.out.println(imageList.size()); // DEBUG
                return;
            case 2:
                biFiltered = rescaleInput(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 3:
                biFiltered = rndShiftRescaleImage(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
            case 4:
                if (compIndex >= 1) {
                    System.out.println(compIndex);
                    frame.remove(panel2);
                    compIndex = 0;
                    repaint();
                } else return;
            case 5: //ADDITION
                biFiltered = imageAddtion(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 6: //SUBTRACTION
                biFiltered = imageSubtraction(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 7: //MULTIPLICATION
                biFiltered = imageMultiplication(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 8: //DIVISION
                biFiltered = imageDivision(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 9: //NOT
                biFiltered = imageNot(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 10: //AND
                biFiltered = imageAnd(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 11: //OR
                biFiltered = imageOr(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 12: //XOR
                biFiltered = XOr(bi, lab3Image);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return; 
            case 13: //LOG
                biFiltered = logFunction(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 14: //POWER-LAW
                biFiltered = powerInput(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 15: //LOOK-UP
                biFiltered = randomLookUpFunction(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;
            case 16: //BPS
                biFiltered = bitPlaneSliceFunction(bi);
                imageList.add(biFiltered);
                topIndex++;
                currIndex++;
                return;  

        }

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("undoAction")) {
            // UNDO FUNCTION
            if (currIndex >= 1) {
                currIndex--;
                prevIndex = currIndex;
                System.out.println("ARRAY LIST SIZE IS: " + imageList.size());
                System.out.println("Current Index is:" + currIndex);
                biFiltered = imageList.get(prevIndex);
                repaint();
            } else
                return;

        } else if (e.getActionCommand().equals("SetFilter_Comparision_Button")) {
            if (compIndex == 0) {
                compIndex++;
                frame.setLayout(new GridLayout(1, 2));
                Demo de2 = new Demo();
                frame.add("Center", de2);
                frame.add("North", panel2);
                frame.pack();
                frame.setVisible(true);
                repaint();
            }

        } else {
            JComboBox cb = (JComboBox) e.getSource();
            if (cb.getActionCommand().equals("SetFilter")) {
                setOpIndex(cb.getSelectedIndex());
                repaint();
            } else if (cb.getActionCommand().equals("SetFilter_Comparision")) {
                if (compIndex == 0) {
                    System.out.println("I have been clicked");
                    compIndex++;
                    frame.setLayout(new GridLayout(1, 2));
                    Demo de2 = new Demo();
                    JPanel panel2 = new JPanel();
                    frame.add("Center", de2);
                    frame.add("North", panel2);
                    frame.pack();
                    frame.setVisible(true);
                    repaint();
                }
          
            } else if (cb.getActionCommand().equals("Formats")) {
                String format = (String) cb.getSelectedItem();
                File saveFile = new File("savedimage." + format);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(saveFile);
                int rval = chooser.showSaveDialog(cb);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    saveFile = chooser.getSelectedFile();
                    try {
                        ImageIO.write(biFiltered, format, saveFile);
                    } catch (IOException ex) {
                    }
                }
            }
        }
    };

    public static void main(String s[]) {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo de = new Demo();
        frame.add("Center", de);
        

        //ORIGINAL IMAGE SELECTION
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());

        //COMPARISON IMAGE SELECTION
        /*JComboBox compare = new JComboBox<String>(de.getDescriptions_Comparisions());
        compare.setActionCommand("SetFilter_Comparision");
        compare.addActionListener(de);*/

        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        
        //PANEL
        JPanel panel = new JPanel();
        container.add(panel);
        panel.add(new JLabel("Image Selection: "));
        panel.add(choices);
        panel.add(new JLabel("Compare With: "));
        //panel.add(compare);
        panel.add(new JLabel("Save As"));
        panel.add(formats);

        //UNDO & REDO
        JButton undo = new JButton("undo");
        undo.setActionCommand("undoAction");
        undo.addActionListener(de);
        panel.add(undo);

        //COMPARE BUTTON
        JButton compareButton = new JButton("Compare");
        compareButton.setActionCommand("SetFilter_Comparision_Button");
        compareButton.addActionListener(de);
        panel.add(compareButton);

   
        frame.add("North", panel);
        frame.pack();
        frame.setVisible(true);
    }
}