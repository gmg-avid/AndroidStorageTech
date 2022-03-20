To get this library into your project

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.gmg-avid:AndroidStorageTech:Tag'
	}

# AndroidStorageTech

The Android Storage Library, this library handle the scope storage and basic storage method. 
This Android introduce the scope storage from andoid 10 and it is optional. From Android 11 it is mandatory one.

In this library we handle the scope storage from android 11. So, in order use this library add legacy storage true to avoid scope storage on the android 10

 android:requestLegacyExternalStorage="true" under application tag.

This Library are used to create, modify, delete the file and also get their details

READ STORAGE PERMISSION and WRITE STORAGE PERMISSION are requied on android 10

From Android 11, READ STORAGE PERMISSION are required if you are accessing other file which is not owned by our app

# To handle the file logic using path

		val file : MTFile = MTFile(filePath)
		
		// we need to source - The source may be filepath, uri, inputstream and media id

		file.setSourceUri(uri)   //To set source  as uri 
		
		file.setSourceMediaId(mediaId) //To set source as media id
		
		file.setSourceFilePath(localFilePath)  //To set source as local file path

		file.setSourceInputStream(inputStream) //To set source as input stream
		
		After Setting source on the file
		
		//To create the file 

		file.createFile()	
		
		//To update the file
		
		file.updateFile()	
		
		//To delete File
		
		file.deleteFile() 
		
		//To get file length
		
		file.getLength()
		
		//To get file duration
		
		file.getDuration()
		
		//To check whether the file is exist

		file.isFileExist()
		
		//To get uri
		
		file.getUri()
		
		//To get mime type 
		
		file.getMimeType()
		
		//To get input stream
		
		file.getInputStream()
		
		//To get output stream
		
		file.getOutputStream()
		
		//To get relative path
		
		file.getRelativePath()
		
		//To get display name
		
		file.getDisplayName()
		
		//To get file details
		
		file.getFileDetails()  
		
- The file details return data class that contians media id, filepath, relative path, display name, uri, length, duration, mime type, uri
		

+ 	While performing operation, exception will throw and security exception throw from android 10 - more details please check offical android scope storage documentation. To handle Error exception initalise the error call back 
+ 	How to set Error call back - Modify and Delete not owned file throw security exception. Handle the security exception with intent sender if user allow the  action on result code equal result ok and perform same operation once got permission and also check the sample project for more details
		
		val deletePickerResult : ActivityResultLauncher<IntentSenderRequest> by lazy {
			 registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(), ActivityResultCallback { it ->
           if (it.resultCode == RESULT_OK) {
               	TODO("implement Logic")
            }
       })
		}

		file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
        override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
           val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
           deletePickerResult.launch(intentSenderRequest)
        }

        override fun onThrowingException(exception: Exception) {
           TODO("implement Logic")
        }
     })

# To handle the uri logic

		val uri : MTUri = MTUri(uri)

		//To delete the file using uri
		
		uri.delete()

		// To modify the file we can set source uri 
	
		uri.setSourceUri(uri)   //To set source  as uri 
		
		uri.setSourceMediaId(mediaId) //To set source as media id
		
		uri.setSourceFilePath(localFilePath)  //To set source as local file path

		uri.setSourceInputStream(inputStream) //To set source as input stream

		//To update and modify

		uri.updateFile()

		//To create file with having uri you can send path details where we can create file with this uri details

		uri.createFile(filePath)

		//other operation like file access logic instead of filedetails here we get uridetails
		
		uri.getUriDetails()

+ To handle the media id logic

		val media : MTMediaId = MTMediaId(mediaId)
		
		//To delete the file using id
		
		media.delete()

		//To create file with having id you can send path details where we can create file with this media id
		
		media.createFile(filePath)
		
		// To modify the file we can set source uri 
	
		media.setSourceUri(uri)   //To set source  as uri 
		
		media.setSourceMediaId(mediaId) //To set source as media id
		
		media.setSourceFilePath(localFilePath)  //To set source as local file path

		media.setSourceInputStream(inputStream) //To set source as input stream

		//To update and modify

		media.updateFile()

+ To handle the multi modify and delete with user permission in android 11

on Android 11, Android introduce scope storage on this concept not owned cannot delete without permission. So, the android introduce single user permission to modify and delete multi file. For details check android documentation and check sample working state.


	val multiFileOperation : MTMultiFile = MTMultiFile()
	
	//To delete multiple file handle error intialize to handle security exception as per above

	multiFileOperation.deleteMultiMediaFile(filePathList)
	
	multiFileOperation.deleteMultiMediaId(mediaIdList)
	
	multiFileOperation.deleteMultiMediaUri(uriList)
	
	//To modify multiple file
	
	multiFileOperation.modifyMultiMediaFile(filePathList)
	
	multiFileOperation.modifyMultiMediaId(mediaIdList)
	
	multiFileOperation.modifyMultiMediaUri(uriList)
	

This library are useful handle the storage system with scope storage and basic storage. This will handle the storage related Work. But, still we need to scope storage perform and give valid a file path from android 11. So, Learn Regarding Scope Storage.

...
