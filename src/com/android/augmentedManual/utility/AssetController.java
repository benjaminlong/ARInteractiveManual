package com.android.augmentedManual.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Used to copy application assets from the application package
 * onto the device. 
 * 
 * @author tim.oppermann
 *
 */
public class AssetController {

	private Application mApplicationContext;


	public AssetController( Application applicationContext )
	{
		mApplicationContext = applicationContext;
	}


	private HashMap<String, String> mAssets = new HashMap<String, String>();
	private boolean mIsExtracted = false;
	/**
	 * Extract all the assets.
	 * <p> If application has lots of assets, this should not be called
	 * on main thread
	 * @return true on success
	 */
	public  boolean  extractAssets()
	{

		try
		{
			if ( !mIsExtracted  )
			{
				extractAssetFiles(mApplicationContext, mApplicationContext.getAssets(), "");
				mIsExtracted = true;

			}

		}
		catch (Exception e)
		{
			Logger.logException(e);
			return false;
		}
		return mIsExtracted;
	}

	/**
	 * Get extracted asset file path.
	 * @param filename File name
	 * @return File path or null if does not exists or assets are not extracted
	 */
	public String getAssetPath(final String filename)
	{
		return mAssets.get(filename.toLowerCase());
	}



	/**
	 * Extracts assets files to device memory.
	 * Overwrites the files if in debug-mode. 
	 * 
	 * @param assetManager AssetManager object
	 * @param parentPath parent path for asset files
	 * @throws IOException
	 */
	private void extractAssetFiles(Context context, AssetManager assetManager, String parentPath) throws IOException
	{
		String targetPath;
		InputStream is = null;

		File f = context.getFilesDir();

		targetPath = f.getAbsolutePath();

		for(String fileName : assetManager.list(parentPath))
		{

			Logger.log("assetManager.list: "+fileName);


			if(!(fileName.equals("webkit") || fileName.equals("sounds") || fileName.equals("images")))
			{

				String filepath = null;

				try
				{
					if (parentPath.length() == 0)
					{
						filepath = targetPath+"/"+fileName;
						is = assetManager.open(fileName, AssetManager.ACCESS_BUFFER);
					}
					else
					{
						filepath = targetPath+"/"+parentPath+"/"+fileName;
						is = assetManager.open(parentPath+"/"+fileName, AssetManager.ACCESS_BUFFER);
					}
					File file = new File(filepath);
					if ( file.isDirectory() )
					{
						file.mkdirs();
						
					}
					else
					{
					// Do not override by default
					copyFile(is, filepath, Logger.isDebuggable);
					}
					mAssets.put(fileName.toLowerCase(), filepath);
					mAssets.put(parentPath.toLowerCase()+"/"+fileName.toLowerCase(), filepath);
				}
				catch(Exception ex)
				{
				

					//maybe this a directory
					File file = new File(filepath);
			
					file.mkdirs();
			
					mAssets.put(fileName.toLowerCase(), filepath);
					mAssets.put(parentPath.toLowerCase()+"/"+fileName.toLowerCase(), filepath);
					Logger.log(Log.DEBUG, "Create directory: "+fileName);
					String nextDir = parentPath;
					if ( parentPath.length() > 0) 
					{
						nextDir += "/";
					}
					nextDir += fileName;
					extractAssetFiles(context, assetManager, nextDir);
					

				}
				finally
				{
					if(is != null){
						is.close();
					}
				}
			}
			else
			{
				Logger.log(Log.WARN, "Ignored: "+fileName);
			}
		}
	}

	/**
	 * Helper function for coping
	 * 
	 * @param is
	 *            InputStream of the file to read
	 * @param path
	 *            where to copy the file
	 * @throws IOException
	 */
	private static void copyFile(InputStream is, String path, boolean override) throws IOException {
		byte[] buffer = new byte[4096];
		int len;
		File file;

		FileOutputStream fos = null;
		try {
			file = new File(path);

			boolean copyContents = false;

			if (file.exists()) 
			{
				//log(Log.WARN, "File already exists: "+path);

				if (override)
				{
					//log(Log.WARN, "Deleting existing file: "+path);
					file.delete();
					copyContents = true;
				}
			}
			else
				copyContents = true;

			if (copyContents)
			{
				file.createNewFile();

				//log("Copying contents: " + file);

				// copy the file from the assets
				fos = new FileOutputStream(file);
				while ((len = is.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

			}
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}



}
