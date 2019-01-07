package com.example.jdavid004.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import com.android.rssample.ScriptC_greyRs;
import com.android.rssample.ScriptC_egalisationHistogrammeRs;
import com.android.rssample.ScriptC_colorizeRs;
import com.android.rssample.ScriptC_redOnlyRs;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewIndex;
    private Bitmap img;

    /* bouton changement d'images */
    private Button restartButton;

    /* variable pour changement d'images */
    private int cptImg = 0;

    /* boutons algo java */
    private Button greyButton;
    private Button colorizeButton;
    private Button redOnlyButton;
    private Button dynamiquecontrasteV1Button;
    private Button dynamiqueContrasteV2Button;
    private Button dynamiqueContrasteRGBButton;
    private Button egalisationHistogrammeButton;
    private Button egalisationHistogrammeRGBButton;
    private Button flouButton;
    private Button contoursButton;


    /* bouton algo renderScript */
    private Button greyRsButton;
    private Button colorizeRsButton;
    private Button redOnlyRsButton;
    private Button egalisationHistogrammeRsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = false;
        option.inMutable = true;

        imageViewIndex = findViewById(R.id.contours);
        img = BitmapFactory.decodeResource(getResources(), R.drawable.contours,option);

        /* on cache toute les autres images pour en afficher qu'une */
        findViewById(R.id.bordeaux).setVisibility(View.INVISIBLE);
        findViewById(R.id.rgbcontraste).setVisibility(View.INVISIBLE);
        findViewById(R.id.poivron).setVisibility(View.INVISIBLE);
        findViewById(R.id.contraste).setVisibility(View.INVISIBLE);

        /* affichage des boutons pour les fonctions java */
        greyButton = findViewById(R.id.greyButton);
        greyButton.setOnClickListener(greyButtonListener);

        colorizeButton = findViewById(R.id.colorizeButton);
        colorizeButton.setOnClickListener(colorizeButtonListener);

        redOnlyButton = findViewById(R.id.redOnlyButton);
        redOnlyButton.setOnClickListener(redOnlyButtonListener);

        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(restartButtonListener);

        dynamiquecontrasteV1Button = findViewById(R.id.dynamiquecontrasteV1Button);
        dynamiquecontrasteV1Button.setOnClickListener(dynamiquecontrasteV1ButtonListener);

        dynamiqueContrasteV2Button = findViewById(R.id.dynamiqueContrasteV2Button);
        dynamiqueContrasteV2Button.setOnClickListener(dynamiqueContrasteV2ButtonListener);

        egalisationHistogrammeButton = findViewById(R.id.egalisationHistogrammeButton);
        egalisationHistogrammeButton.setOnClickListener(egalisationHistogrammeButtonListener);

        egalisationHistogrammeRGBButton = findViewById(R.id.egalisationHistogrammeRGBButton);
        egalisationHistogrammeRGBButton.setOnClickListener(egalisationHistogrammeRGBButtonListener);

        dynamiqueContrasteRGBButton = findViewById(R.id.dynamiqueContrasteRGBButton);
        dynamiqueContrasteRGBButton.setOnClickListener(dynamiqueContrasteRGBButtonListener);

        flouButton = findViewById(R.id.flouButton);
        flouButton.setOnClickListener(flouButtonListener);

        contoursButton = findViewById(R.id.contoursButton);
        contoursButton.setOnClickListener(contoursButtonListener);


        /* affichage des boutons pour les fonctions renderScript */

        greyRsButton = findViewById(R.id.greyRsButton);
        greyRsButton.setOnClickListener(greyRsButtonListener);

        egalisationHistogrammeRsButton = findViewById(R.id.egalisationHistogrammeRsButton);
        egalisationHistogrammeRsButton.setOnClickListener(egalisationHistogrammeRsButtonListener);

        colorizeRsButton = findViewById(R.id.colorizeRsButton);
        colorizeRsButton.setOnClickListener(colorizeRsButtonListener);

        redOnlyRsButton = findViewById(R.id.redOnlyRsButton);
        redOnlyRsButton.setOnClickListener(redOnlyRsButtonListener);

        imageViewIndex.setImageBitmap(img);
    }

    /* affiche le texte pour les dimensions des images */
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        int width = imageViewIndex.getWidth();
        int height = imageViewIndex.getHeight();

        String w = Integer.toString(width);
        String h = Integer.toString(height);

        TextView textView = (TextView) findViewById(R.id.tailleImg);
        textView.setText(w + " x " + h);
    }

    /* toGrey non optimisé avec setPixel */

    /*public void toGrey(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int newColor = bmp.getPixel(x,y);
                int R = Color.red(newColor);
                int G = Color.green(newColor);
                int B = Color.blue(newColor);
                int grey = (int)(0.3 * R + 0.59 * G + 0.11 * B);
                bmp.setPixel(x,y,Color.rgb(grey,grey,grey));
            }
        }

    }*/

    /* toGrey optimisé avec setPixels */

    /* grise tout les pixels de l'image */
    public void toGrey(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i = 0; i < w*h; ++i){
            int R = Color.red(pixels[i]);
            int G = Color.green(pixels[i]);
            int B = Color.blue(pixels[i]);
            int grey = (int)(0.3 * R + 0.59 * G + 0.11 * B);
            pixels[i] = Color.rgb(grey,grey,grey);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* change la teinte de l'image aléatoirement */
    public void toColorize(Bitmap bmp){
        Random r = new Random();
        int valeur = r.nextInt(360);

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        float[] hsvTab = new float[3];
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i = 0; i < w*h; ++i){
            /* conversion RGB vers HSV */
            Color.RGBToHSV(Color.red(pixels[i]),Color.green(pixels[i]),Color.blue(pixels[i]),hsvTab);
            hsvTab[0] = valeur;
            /* on reconvertit en RGB */
            pixels[i] = Color.HSVToColor(hsvTab);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* conserve les pixels rouges de l'image, grise les autres */

    public void toRedOnly(Bitmap bmp){
        float[] hsvTab = new float[3];
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i = 0; i < w*h; ++i){
            /* conversion RGB vers HSV */
            Color.RGBToHSV(Color.red(pixels[i]),Color.green(pixels[i]),Color.blue(pixels[i]),hsvTab);
            /* si le pixel n'est pas de teinte rouge, on le grise */
            if(hsvTab[0] > 15 && hsvTab[0] < 345){
                int R = Color.red(pixels[i]);
                int G = Color.green(pixels[i]);
                int B = Color.blue(pixels[i]);
                int grey = (int)(0.3 * R + 0.59 * G + 0.11 * B);
                pixels[i] = Color.rgb(grey,grey,grey);
            }

        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /*TP3 Contraste*/

    /*1) Extension de dynamique */

    /* Exercice 1 Question 1 */

    /* augmente le contraste de l'image par extension dynamique */
    public void dynamiquecontrasteV1(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        int[] lut = new int[256];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int min = Color.red(pixels[0]);
        int max = Color.red(pixels[0]);
        int grey;

        /* calcule la plus petite et plus grande valeur de gris dans l'image */
        for(int i = 1; i < w*h; ++i){
            grey = Color.red(pixels[i]);
            if(min > grey){
                min = grey;
            }
            if(max < grey){
                max = grey;
            }
        }

        //initialisation de la lut
        for(int ng = 0; ng < 256; ng++){
            lut[ng] = (255 * (ng - min)) / (max - min);
        }
        //calcul de la transformation
        for(int i = 0; i < w*h; i++){
            grey = Color.red(pixels[i]);
            pixels[i] = Color.rgb(lut[grey],lut[grey],lut[grey]);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* Exercice 1 Question 2 */

    /* diminue le contraste en resserant l'histogramme sur une plage de valeurs plus petite que la dynamique initiale */
    public void dynamiqueContrasteV2(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int min;
        int max;
        int grey;
        int[] histogramme = new int[256];

        /* on prend 1% des premiers pixels de l'image */
        int borneMin = (w*h) / 100;
        /* on prend 1% des derniers pixels de l'image */
        int borneMax = pixels.length - borneMin;


        for(int i = 0; i < borneMin; ++i){

            grey = Color.red(pixels[i]);

            histogramme[grey] ++;
        }

        for(int i = pixels.length-1; i > borneMax; --i){

            grey = Color.red(pixels[i]);
            histogramme[grey] ++;

        }
        int indice = 0;
        while(histogramme[indice] == 0){
            indice ++;
        }
        min = indice;

        indice = 255;
        while(histogramme[indice] == 0){
            indice --;
        }
        max = indice;
        for(int i = 0; i < w*h; ++i){
            grey = Color.red(pixels[i]);
            grey = ((255 * (grey - min)) / (max - min)) ;
            if(grey < 0){
                grey = 255;
            }

            pixels[i] = Color.rgb(grey,grey,grey);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* Exercice 1 Question 3 */

    /* augmente le contraste d'une image de couleurs */

    public void dynamiqueContrasteRGB(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int minR = Color.red(pixels[0]);;
        int maxR = Color.red(pixels[0]);

        int minG = Color.green(pixels[0]);
        int maxG = Color.green(pixels[0]);

        int minB = Color.blue(pixels[0]);
        int maxB = Color.blue(pixels[0]);

        int R;
        int G;
        int B;

        for(int i = 1; i < w*h; ++i){
            R = Color.red(pixels[i]);
            G = Color.green(pixels[i]);
            B = Color.blue(pixels[i]);
            /* couleur Rouge */
            if(minR > R){
                minR = R;
            }
            if(maxR < R){
                maxR = R;
            }

            /* couleur Vert */
            if(minG > G){
                minG = G;
            }
            if(maxG < G){
                maxG = G;
            }

            /* couleur Bleu */
            if(minB > B){
                minB = B;
            }
            if(maxB < B){
                maxB = B;
            }
        }

        for(int i = 0; i < w*h; ++i){
            R = Color.red(pixels[i]);
            G = Color.green(pixels[i]);
            B = Color.blue(pixels[i]);
            R = (255 * (R - minR)) / (maxR - minR);
            G = (255 * (G - minG)) / (maxG - minG);
            B = (255 * (B - minB)) / (maxB - minB);

            pixels[i] = Color.rgb(R,G,B);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /*2) Egalisation d'histogramme */

    /* Exercice 2 Question 1 */

    /* augmentation du contraste par egalisation d'histogramme */

    public void egalisationHistogramme(Bitmap bmp){ // N = nb_pixel
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        int[] histogramme = new int[256];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int grey;
        int color;
        int nb_pixel = w*h;

        for(int i = 1; i < nb_pixel; ++i){
            grey = Color.red(pixels[i]);
            histogramme[grey] ++;
        }

        for(int i = 1; i < 256; ++i){
            histogramme[i] = histogramme[i] + histogramme[i-1];
        }

        for(int i = 0; i < nb_pixel; ++i){
            grey = Color.red(pixels[i]);
            color = (histogramme[grey]*255)/nb_pixel;
            pixels[i] = Color.rgb(color,color,color);

        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* Exercice 2 Question 2 */

    /* augmentation du contraste par egalisation d'histogramme sur une image en couleur */

    public void egalisationHistogrammeRGB(Bitmap bmp){ // N = nb_pixel
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        int[] histogramme = new int[256];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        int color;
        int nb_pixel = w*h;

        for(int i = 1; i < nb_pixel; ++i){
            color = ((Color.red(pixels[i])) + (Color.blue(pixels[i])) + (Color.green(pixels[i])))/3 ;
            histogramme[color] ++;
        }

        for(int i = 1; i < 256; ++i){
            histogramme[i] = histogramme[i] + histogramme[i-1];
        }

        for(int i = 0; i < nb_pixel; ++i){
            int R = Color.red(pixels[i]);
            int newR = (histogramme[R]*255) / pixels.length;
            int G = Color.green(pixels[i]);
            int newG = (histogramme[G]*255) / pixels.length;
            int B = Color.blue(pixels[i]);
            int newB = (histogramme[B]*255) / pixels.length;
            pixels[i] = Color.rgb(newR,newG,newB);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    /* Tp4 Traitement d'image : flou */
    /* 1) Première étape : implémentation java */

    /* Flou Gaussien */

    /* sert à diviser avec des nombres négatifs */
    public int divide(int a, int b){
        int resultat = 0;
        if(a < 0){
            resultat = (a * -1)  /b;
            return (-1) * resultat;
        }
        return a/b;
    }

    /* on applique un masque sur un pixel d'une image */
    public int[] masque(int[] pixels, int indice, int w, int[] masque){
        int indiceX = indice % w;
        int indiceY = Math.round(indice / w);
        int R = 0;
        int G = 0;
        int B = 0;

        int cptMasque = 0;
        for(int x = indiceX - 1; x <= indiceX + 1; x++){
            for(int y = indiceY - 1; y <= indiceY + 1; y++){
                int indiceMatrice = y * w + x;

                R = R + (Color.red(pixels[indiceMatrice]) * masque[cptMasque]) ;
                G = G + (Color.green(pixels[indiceMatrice]) * masque[cptMasque]);
                B = B + (Color.blue(pixels[indiceMatrice]) * masque[cptMasque]);
                cptMasque++;
            }
        }

        /* normalise la valeur */
        int normalisation = 0;
        for(int i = 0; i < 9; i++){
            if(masque[i] > 0){
                normalisation ++;
            }
        }

        R = divide(R,normalisation);
        G = divide(G,normalisation);
        B = divide(B,normalisation);

        /* on renvoit la couleur */
        int[] color =  { R, G , B};
        return color;

    }

    /* floute l'image avec une matrice moyenneur et mets en noir les bords */
    public void flou(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        int[] pixelsBackup = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        bmp.getPixels(pixelsBackup,0,w,0,0,w,h);
        int[] masqueMoyenneur = {1,1,1,1,1,1,1,1,1};
        for(int y = 0; y < h ; y++){
            for(int x = 0; x < w; x++){
                int indice = y * w + x;

                if(x == 0 || y == 0){
                    pixelsBackup[indice] = Color.rgb(0,0,0);
                }
                else if(x == w - 1 || y == h - 1){
                    pixelsBackup[indice] = Color.rgb(0,0,0);
                }
                else{
                    int[] color = masque(pixels,indice,w,masqueMoyenneur);
                    pixelsBackup[indice] = Color.rgb(color[0], color[1], color[2]);

                }
            }
        }
        bmp.setPixels(pixelsBackup,0,w,0,0,w,h);
    }

    /* dessine les contours */

    public void contours(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        int[] pixelsBackup = new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        bmp.getPixels(pixelsBackup,0,w,0,0,w,h);

        int[] gradientH1 = {-1,0,1,-1,0,1,-1,0,1};
        int[] gradientH2 = {-1,-1,-1,0,0,0,1,1,1};
        /* calcul du gradient */
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){

                int indice = y * w + x;

                /* gestion des bords : Voir masque des différences diapo */
                if(x == 0 || x == w - 1 || y == 0 || y == w - 1 ){
                    pixelsBackup[indice] =  Color.rgb(0,0,0);
                }
                else{

                    /* calcul matrice H1 */

                    int[] Gx = masque(pixels,indice, w, gradientH1);


                    /* calcul matrice H2 */

                    int[] Gy = masque(pixels,indice,w,gradientH2);

                    int module = (int) Math.round( Math.sqrt(  Gx[0]*Gx[0]  +  Gy[0]*Gy[0] ));


                    pixelsBackup[indice] = Color.rgb(module,module,module);
                }
            }
        }
        bmp.setPixels(pixelsBackup,0,w,0,0,w,h);


    }

    /* Fonction Renderscript */

    private  void  toGreyRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);

        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType());

        //3)  Creer le  script
        ScriptC_greyRs  greyRsScript = new  ScriptC_greyRs(rs);

        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        // ...
        //6)  Lancer  le noyau
        greyRsScript.forEach_greyRs(input , output);

        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);

        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        greyRsScript.destroy (); rs.destroy ();
    }


    private  void toColorizeRs(Bitmap  bmp) {
        Random rand = new Random();
        int nombreAleatoire = rand.nextInt(360);
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);

        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType());

        //3)  Creer le  script
        ScriptC_colorizeRs  colorizeRsScript = new  ScriptC_colorizeRs(rs);

        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        colorizeRsScript.set_t(nombreAleatoire);
        //6)  Lancer  le noyau
        colorizeRsScript.forEach_colorizeRs(input , output);

        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);

        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        colorizeRsScript.destroy (); rs.destroy ();
    }

    private void toRedOnlyRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);

        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType());

        //3)  Creer le  script
        ScriptC_redOnlyRs  redOnlyRsScript = new  ScriptC_redOnlyRs(rs);

        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        // ...
        //6)  Lancer  le noyau
        redOnlyRsScript.forEach_redOnlyRs(input , output);

        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);

        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        redOnlyRsScript.destroy (); rs.destroy ();
    }

    private void toEgalisationHistogrammeRs(Bitmap bmp) {
        //Get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //Create new bitmap
        Bitmap res = bmp.copy(bmp.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(this);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        //Create script from rs file.
        ScriptC_egalisationHistogrammeRs histEqScript = new ScriptC_egalisationHistogrammeRs(rs);

        //Set size in script
        histEqScript.set_size(width*height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();

        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationA.copyTo(bmp);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
    }




    private View.OnClickListener greyButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toGrey(img);
        }
    };

    private View.OnClickListener colorizeButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toColorize(img);
        }

    };

    private View.OnClickListener redOnlyButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toRedOnly(img);
        }

    };

    private View.OnClickListener dynamiquecontrasteV1ButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            dynamiquecontrasteV1(img);
        }

    };

    private View.OnClickListener dynamiqueContrasteV2ButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            dynamiqueContrasteV2(img);
        }

    };

    private View.OnClickListener egalisationHistogrammeButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            egalisationHistogramme(img);
        }

    };

    private View.OnClickListener egalisationHistogrammeRGBButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            egalisationHistogrammeRGB(img);
        }

    };

    private View.OnClickListener dynamiqueContrasteRGBButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            dynamiqueContrasteRGB(img);
        }

    };

    private View.OnClickListener flouButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            flou(img);
        }

    };

    private View.OnClickListener contoursButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            contours(img);
        }

    };


    /* change l'image quand on clique sur le bouton change */
    private View.OnClickListener restartButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            cptImg ++;

            if(cptImg > 4){
                cptImg = 0;
            }
            if(cptImg == 0){
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inScaled = false;
                option.inMutable = true;
                findViewById(R.id.contraste).setVisibility(View.INVISIBLE);
                imageViewIndex = findViewById(R.id.contours);
                imageViewIndex.setVisibility(View.VISIBLE);
                img = BitmapFactory.decodeResource(getResources(), R.drawable.contours,option);
                imageViewIndex.setImageBitmap(img);
            }
            if(cptImg == 1){
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inScaled = false;
                option.inMutable = true;
                findViewById(R.id.contours).setVisibility(View.INVISIBLE);
                imageViewIndex = findViewById(R.id.bordeaux);
                imageViewIndex.setVisibility(View.VISIBLE);
                img = BitmapFactory.decodeResource(getResources(), R.drawable.bordeaux,option);
                imageViewIndex.setImageBitmap(img);
            }
            if(cptImg == 2){
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inScaled = false;
                option.inMutable = true;
                findViewById(R.id.bordeaux).setVisibility(View.INVISIBLE);
                imageViewIndex = findViewById(R.id.rgbcontraste);
                imageViewIndex.setVisibility(View.VISIBLE);
                img = BitmapFactory.decodeResource(getResources(), R.drawable.rgbcontraste,option);
                imageViewIndex.setImageBitmap(img);
            }
            if(cptImg == 3){
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inScaled = false;
                option.inMutable = true;
                findViewById(R.id.rgbcontraste).setVisibility(View.INVISIBLE);
                imageViewIndex = findViewById(R.id.poivron);
                imageViewIndex.setVisibility(View.VISIBLE);
                img = BitmapFactory.decodeResource(getResources(), R.drawable.poivron,option);
                imageViewIndex.setImageBitmap(img);
            }
            if(cptImg == 4){
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inScaled = false;
                option.inMutable = true;
                findViewById(R.id.poivron).setVisibility(View.INVISIBLE);
                imageViewIndex = findViewById(R.id.contraste);
                imageViewIndex.setVisibility(View.VISIBLE);
                img = BitmapFactory.decodeResource(getResources(), R.drawable.contraste,option);
                imageViewIndex.setImageBitmap(img);
            }

            int width = imageViewIndex.getWidth();
            int height = imageViewIndex.getHeight();

            String w = Integer.toString(width);
            String h = Integer.toString(height);

            TextView textView = (TextView) findViewById(R.id.tailleImg);
            textView.setText(w + " x " + h);

        }

    };

    /* fonction RS */

    private View.OnClickListener greyRsButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toGreyRS(img);
        }
    };

    private View.OnClickListener egalisationHistogrammeRsButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toEgalisationHistogrammeRs(img);
        }
    };

    private View.OnClickListener colorizeRsButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toColorizeRs(img);
        }
    };

    private View.OnClickListener redOnlyRsButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            toRedOnlyRS(img);
        }
    };


}