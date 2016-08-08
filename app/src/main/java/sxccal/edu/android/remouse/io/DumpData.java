package sxccal.edu.android.remouse.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DumpData {

    private String mFilePath;
    private FileWriter mFileWriter;
    private File mDumpDir;

    public boolean fileClosed = false;

    public  DumpData(String directory, boolean isLinear) {
        try {
            createAppDirectory(directory);
            if(!isLinear) {
                mFileWriter = new FileWriter(mFilePath + "/acc_normal.csv", true);
            } else {
                mFileWriter = new FileWriter(mFilePath + "/acc_linear.csv", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DumpData(String directory) {
        try {
            createAppDirectory(directory);
            mFileWriter = new FileWriter(mFilePath + "/gyroscope.csv", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAppDirectory(String directory) {
        mFilePath = directory + "/Remouse";
        mDumpDir  = new File(mFilePath);
        if (!mDumpDir.exists())  mDumpDir.mkdir();
    }

    public void dumpToFile(long currentTime, float values[]) {
        try {
            String s = currentTime + ", ";
            s += values[0] + ", ";
            s += values[1] + ", ";
            s += values[2] + "\n";
            mFileWriter.write(s);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            mFileWriter.close();
            fileClosed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        File[] files = mDumpDir.listFiles();
        for(File file: files)
            file.delete();
    }
}
