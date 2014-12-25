import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Kelas utama yang dapat melakukan komunikasi dengan FTP dengan membuat objek
 * dari kelas ini.
 * 
 * @author muhammadgilangjanuar at SEMAR Development
 */
public class Online {
	public FTPClient ftp;

	/**
	 * Konstruktor default bila parameter untuk object dari kelas Online tidak
	 * didefinisikan saat objek dibuat.
	 */
	public Online() {
		ftp = null;
	}

	/**
	 * Konstruktor dari kelas Online yang mendefinisikan String untuk server,
	 * username, dan password di definisikan dalam parameter saat objek dibuat.
	 * 
	 * @param host
	 *            Lihat method connect().
	 * @param user
	 *            Lihat method connect().
	 * @param pwd
	 *            Lihat method connect().
	 */
	public Online(String host, String user, String pwd) {
		ftp = null;
		conect(host, user, pwd);
	}

	/**
	 * Method utama dalam kelas Online yang bertugas menyambungkan koneksi
	 * dengan FTP untuk memulai melakukan operasi lainnya semisal upload,
	 * getFile, remove, dan lain-lain.
	 * 
	 * @param host
	 *            Sebuah string yang merepresentasikan server host FTP yang
	 *            digunakan misal "ftp.semardev.com".
	 * @param user
	 *            String yang merepresentasikan nama user dari pengguna FTP.
	 * @param pwd
	 *            String yang mendefinisikan password dari user pemilik FTP.
	 */
	public void conect(String host, String user, String pwd) {
		ftp = new FTPClient();
		int reply;
		try {
			ftp.connect(host);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}
			ftp.login(user, pwd);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
		} catch (Exception e) {
			System.out
					.print("connection problem and fatal error on connecting server ln 78 "
							+ getClass().getName() + "\n");
		}
	}

	/**
	 * Method khusus yang digunakan untuk melakukan komunikasi dengan FTP dalam
	 * bentuk upload sebuah file.
	 * 
	 * @param localFileFullName
	 *            Sebuah string yang merepresentasikan nama file atau alamat
	 *            dari file tersebut berada dalam komputer client.
	 * @param fileName
	 *            String yang mendefinisikan nama file ketika sudah diupload di
	 *            FTP.
	 * @param hostDir
	 *            String yang mendefinisikan nama directory pada FTP yang akan
	 *            digunakan sebagai directory upload file lokal. Format
	 *            penulisannya menggunakan tanda "/" di akhir nama directory
	 *            misal "project/".
	 */
	public void uploadFile(String localFileFullName, String fileName,
			String hostDir) {
		try (InputStream input = new FileInputStream(
				new File(localFileFullName))) {
			this.ftp.storeFile(hostDir + fileName, input);
		} catch (FileNotFoundException e) {
			System.out.print("file not found on upoad data ln 105 "
					+ getClass().getName() + "\n");
		} catch (IOException e) {
			System.out.print("fatal error on upload data ln 108 "
					+ getClass().getName() + "\n");
		}
	}

	/**
	 * Method khusus yang bertugas untuk memutuskan koneksi dengan FTP.
	 */
	public void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException e) {
				System.out.print("fatal error on disconnect ln 122 "
						+ getClass().getName() + "\n");
			}
		}
	}

	/**
	 * Method khusus untuk melakukan komunikasi dengan FTP dalam bentuk
	 * mendapatkan file text dari FTP tersebut dan mengembalikannya dalam bentuk
	 * InputStreamReader.
	 * 
	 * @param urlFile
	 *            String yang mendefinisikan alamat URL file lengkap dengan
	 *            protokolnya (http://, https://) misal
	 *            "http://semardev.com/file.txt".
	 * @return Sebuah object InputStreamReader yang dapat difungsikan dengan
	 *         BufferedReader atau Scanner dalam penggunaannya.
	 */
	public InputStreamReader getFileText(String urlFile) {
		URL url;
		InputStreamReader isr = null;
		try {
			url = new URL(urlFile);
			URLConnection con = url.openConnection();
			isr = new InputStreamReader(con.getInputStream());
		} catch (FileNotFoundException e) {
			System.out.print("error file not found on get file ln 148 "
					+ getClass().getName() + "\n");
		} catch (Exception e) {
			System.out.print("fatal error on get file text ln 151 "
					+ getClass().getName() + "\n");
		}
		return isr;
	}

	/**
	 * Method khusus yang digunakan untuk men-download file dari FTP yang dapat
	 * berupa file selain dari file text.
	 * 
	 * @param fileName
	 *            Nama file yang akan di download dari FTP dan akan disimpan
	 *            dengan nama yang sama di local path.
	 * @param pathRemote
	 *            Directory yang ada di FTP yang merupakan tempat dari file
	 *            tersebut berada.
	 * @param pathLocal
	 *            Directory dalam perangkat yang akan digunakan sebagai tempat
	 *            menyimpan file tersebut.
	 */
	public void getFile(String fileName, String pathRemote, String pathLocal) {
		try {
			String remoteFile1 = "/" + pathRemote + fileName;
			File downloadFile1 = new File(pathLocal + "/" + fileName);
			OutputStream outputStream1 = new BufferedOutputStream(
					new FileOutputStream(downloadFile1));
			boolean success = ftp.retrieveFile(remoteFile1, outputStream1);
			outputStream1.close();
			if (success) {
				System.out.println(fileName
						+ " has been downloaded successfully.");
			}
		} catch (IOException e) {
			System.out.println("fatal error on get file ln 184 "
					+ getClass().getName());
		}
	}

	/**
	 * Method khusus untuk menghapus file tertentu dalam FTP.
	 * 
	 * @param fileToDelete
	 *            Nama file yang akan dihapus pada FTP.
	 */
	public void remove(String fileToDelete) {
		boolean deleted;
		try {
			deleted = ftp.deleteFile(fileToDelete);
			if (!deleted) {
				System.out.print("failed remove data ln 200 "
						+ getClass().getName() + "\n");
				throw new IOException();
			}
		} catch (IOException e) {
			System.out.print("fatal error on remove data ln 205 "
					+ getClass().getName() + "\n");
		}
	}

}