package dlsim.DLSim;
import java.io.*;
import java.util.*;
import java.net.*;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class Preferences
{


  private static File circuits;
  private static File f;
  static HashMap keyvalues=new HashMap();

  public Preferences()
  {

    // the applet cant create files
    if (!AppletMain.isapplet)
    {
      f = new File(System.getProperties().getProperty("user.dir"),"preferences.obj");
      circuits = new File(f.getParent(),File.separator+"circuits");

      if (!f.exists())
      {
        Debug.out("Preferences file not found - creating "+f.getName());
        try
        {
          f.createNewFile();
          FileOutputStream fos = new FileOutputStream(f);
          ObjectOutputStream oos = new ObjectOutputStream(fos);
          oos.writeObject(keyvalues);
          oos.flush();
          fos.close();
          Debug.out("Creating "+circuits);
          circuits.mkdirs();
        }
        catch (IOException e)
        {
          Debug.out("Could not create preferences file!"+f.getName());
        }
      }
      try
      {
        FileInputStream fis  = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);
        keyvalues = ((HashMap)ois.readObject());
      }
      catch (Exception e)
      {
        staticUtils.errorMessage("cant read from preferences file!");
        System.err.println("Error setting preference");
        e.printStackTrace();
      }
    }
  }

  /**
   * Sets this preference in the prefs file
   * @param key The key (e.g. username)
   * @param value The value (e.g. Pete)
   */
  public static void setValue(String key, String value)
  {
    if (AppletMain.isapplet) return;
    // put in hashmap
    if (keyvalues.containsKey(key)) keyvalues.remove(key);
    keyvalues.put(key,value);
    // write file
    try {
      f.delete();
      f.createNewFile();
      FileOutputStream fos = new FileOutputStream(f);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(keyvalues);
      oos.flush();
      fos.close();
    }
    catch (Exception e)
    {
      staticUtils.errorMessage("cant write to preferences file!");
      System.err.println("Error setting preference");
      e.printStackTrace();
    }
  }

  /**
   * Gets a value from the prefrences file
   * @param key The key (e.g. username)
   * @return The value, or null if no value found
   */
  public static String getValue(String key)
  {
   if (AppletMain.isapplet) return null;
    return ((String)keyvalues.get(key));
  }

  /**
   * Gets a value from the prefrences file
   * if the value is null, it is set to defaultvalue
   * and default valure is returned
   * @param key The key (e.g. username)
   * @param defaultvalue The defaule value (e.g. User)
   * @return The value in the prefs file, or defaultvalue if none found
   */
  public static String getValue(String key,String defaultvalue)
  {
     if (AppletMain.isapplet) return defaultvalue;
    String value = getValue(key);
    if (value==null)
    {
      setValue(key,defaultvalue);
      return defaultvalue;
    }
    else
    {
      return value;
    }
  }


  private static URL docsURL=null;

  public static File getCircuitsPath()
  {
    return circuits;
  }

  public static void setDocumentsURL(URL u)
  {
    docsURL=u;
  }

  public static URL getDocumentsURL()
  {
    return docsURL;
  }


}