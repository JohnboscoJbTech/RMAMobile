package jbtechventures.com.rtma.Utility;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jbtechventures.com.rtma.Model.Party;
import jbtechventures.com.rtma.Model.Vote;

public abstract class ApplicationUtil {

    public static String getDeviceID(Context context) {

        TelephonyManager TelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return ;
        }
        String m_szImei = TelephonyMgr.getDeviceId();

        String m_szDevIDShort = "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits

        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // 4 wifi manager, read MAC address - requires
        // android.permission.ACCESS_WIFI_STATE or comes as null
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        // 5 Bluetooth MAC address android.permission.BLUETOOTH required
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();
        System.out.println("m_szBTMAC "+m_szBTMAC);

        // 6 SUM THE IDs
        String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;
        System.out.println("m_szLongID "+m_szLongID);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        byte p_md5Data[] = m.digest();

        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase();

        return m_szUniqueID;
    }

    public static String cleanString(String input) {
        String output = "";
        String text = "";
        if(input.startsWith("\"")) {
            text = input.substring(1, input.length());
        }
        if(text.endsWith("\"")) {
            output = text.substring(0, text.length()-1);
        }
        return output;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();

        /*int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();*/
        /*byte[] x = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap to byte[] stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            x = stream.toByteArray();
            //close stream to save memory
            stream.close();
            byte[] results = new String(x).trim().getBytes();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;*/
    }

    public static byte[] getBytesFromImageFile(String filePath){

        File imagefile = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80 , baos);

        byte[] b = baos.toByteArray();
        return b;
    }

    public static Bitmap getImage(byte[] image) {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
            /*Bitmap b = null;
            b.copyPixelsFromBuffer(image);

            Bitmap bitmap = Bitmap.createBitmap(50, 45, Bitmap.Config.ARGB_8888);

            bitmap.copyPixelsFromBuffer(ImageIO);*/
        }
        else
            return null;
    }

    public static int parseIntFromEditText(EditText editText){
        if(editText == null) return 0;
        return !editText.getText().toString().trim().equals("") ? Integer.parseInt(editText.getText().toString().trim()) : 0;
    }

    public static String parseDate(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return outputFormat.format(date);
    }
    public static Date parseDates(String inputDate) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = null;
        if(inputDate != null){
            try {
                date = format.parse(inputDate);
                //System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static ArrayList<String> getPartyNames(ArrayList<Party> parties){
        ArrayList<String> partyNames = new ArrayList<>();
        for (Party party: parties) {
            partyNames.add(party.Code);
        }
        return partyNames;
    }

    public static Boolean isVotesEqualToPartiesCount(int votesCast, ArrayList<Vote> votes){
        int total = 0;

        for (Vote v: votes) {
            total += v.Count;
        }
        if(total == votesCast)
            return true;
        return false;
    }
}
