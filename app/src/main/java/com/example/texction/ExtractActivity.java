package com.example.texction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExtractActivity extends AppCompatActivity {

    //MyTessOCR myTessOCR;
    ImageView image;
    SharedPreferences sharedPreferences;
    String filePath;
    Mat mGrey;
    Mat rect_kernel;
    Mat iterations=new Mat(1);
    List<MatOfPoint> counters;
    List<String> list;
    Bitmap bmp;
    int rectanx1;
    int rectany1;
    int rectanx2;
    int rectany2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file),MODE_PRIVATE);

        image=findViewById(R.id.image);

        filePath=sharedPreferences.getString("img","");
        setContentView(R.layout.activity_extract);
        OpenCVLoader.initDebug();
        //myTessOCR=new MyTessOCR(this);

        //File file=new File(Environment.getExternalStoragePublicDirectory(
                        //Environment.DIRECTORY_DOWNLOADS)+File.separator+"Texction"+File.separator + filePath);


        //Bitmap myBitmap= BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(
                //Environment.DIRECTORY_DOWNLOADS)+File.separator+"Texction"+File.separator + filePath);
        //Bitmap myBitmap32=myBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
        Mat source= Imgcodecs.imread(getResources().getDrawable(R.drawable.sample).toString());
        Mat destination= new Mat();

        //Converting to Gray Scale
        Imgproc.cvtColor(source,destination,Imgproc.COLOR_RGB2GRAY);

        //Applying threshhold
        Imgproc.threshold(destination,destination,50,255,Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY_INV) ;


        //Specify structure shape and kernel size.
        //Kernel size increases or decreases the area
        //of the rectangle to be detected.
        //A smaller value like (10, 10) will detect
        //each word instead of a sentence.
        Point point=new Point(18,18);
        Size size=new Size();
        rect_kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,size,point);

        //Appplying dilation on the threshold image
        Imgproc.dilate(destination, rect_kernel, iterations );

        //Finding contours
        counters=new ArrayList<>();
        Mat heirarchy=new Mat();
        Imgproc.findContours(destination,counters,heirarchy, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);

        //Coping img
        Mat source2=new Mat();
        source.copyTo(source2);


        Rect reactant;
        Scalar scalar=new Scalar(0,255,0);

        for(MatOfPoint cnt :counters) {
            reactant = Imgproc.boundingRect(cnt);

            //Drawing a rectangle on copied image
            Imgproc.rectangle(source2, reactant.br(), reactant.tl(),scalar, 2) ;

            //Cropping the text block for giving input to OCR
            Mat cropped=new Mat(source2,reactant);

            //Apply OCR on the cropped image
            bmp = Bitmap.createBitmap(cropped.width(), cropped.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped, bmp);
            //String text = myTessOCR.getOCRResult(bmp);


            //Appending the text into file
            //list.add(text);

        }
        String text=list.get(0);

        image.setImageBitmap(bmp);

        Toast.makeText(this,text,Toast.LENGTH_LONG).show();


    }


}