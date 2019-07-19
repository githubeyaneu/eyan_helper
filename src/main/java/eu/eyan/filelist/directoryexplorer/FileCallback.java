package eu.eyan.filelist.directoryexplorer;


public interface FileCallback {

	void directoryCallBack(FileCallbackEvent event);

	void fileCallback(FileCallbackEvent event);

	void progressCallBack(long progress);
}
