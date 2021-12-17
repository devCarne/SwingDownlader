import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class SwingDownloader extends JFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SwingDownloader swingDownloader = new SwingDownloader();
            swingDownloader.setVisible(true);
        });
    }

    public SwingDownloader() {
        super("Swing Downloader");
        setSize(550, 250);
        setLocation(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        JLabel labelTitle = new JLabel("Download File", SwingConstants.CENTER);
        labelTitle.setBounds(75, 25, 370, 15);
        contentPane.add(labelTitle);

        String fileURL = "https://download.springsource.com/release/STS4/4.13.0.RELEASE/dist/e4.22/spring-tool-suite-4-4.13.0.RELEASE-e4.22.0-win32.win32.x86_64.self-extracting.jar";
        JTextField textFieldURL = new JTextField(fileURL);
        textFieldURL.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldURL.setBounds(75, 65, 370, 20);
        contentPane.add(textFieldURL);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setBounds(160, 100, 200, 20);
        contentPane.add(progressBar);

        JButton buttonDownload = new JButton("Download");
        buttonDownload.setBounds(210, 130, 100, 25);
        buttonDownload.addActionListener(new DownloadActionListener(textFieldURL, progressBar, buttonDownload));
        contentPane.add(buttonDownload);
    }
}

class DownloadActionListener implements ActionListener {
    JTextField textFieldURL;
    JProgressBar progressBar;
    JButton buttonDownload;

    public DownloadActionListener(JTextField textFieldURL, JProgressBar progressBar, JButton buttonDownload) {
        this.textFieldURL = textFieldURL;
        this.progressBar = progressBar;
        this.buttonDownload = buttonDownload;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonDownload.setEnabled(false);
        EventQueue.invokeLater(() -> {
            DownloadWorker downloadWorker = new DownloadWorker(textFieldURL, progressBar, buttonDownload);
            downloadWorker.start();
        });
    }
}
class DownloadWorker extends Thread {
    JTextField textFieldURL;
    JProgressBar progressBar;
    JButton buttonDownload;

    public DownloadWorker(JTextField textFieldURL, JProgressBar progressBar, JButton buttonDownload) {
        this.textFieldURL = textFieldURL;
        this.progressBar = progressBar;
        this.buttonDownload = buttonDownload;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(textFieldURL.getText());
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = new BufferedInputStream(url.openStream());
            int fileLength = connection.getContentLength();

            String contentName = textFieldURL.getText();
            String fileName = contentName.substring(contentName.lastIndexOf('1' +1, contentName.length()));

            String saveFile = null;
            try {
                saveFile = new File(".").getCanonicalPath() + "\\files\\" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream outputStream = new FileOutputStream(saveFile);

            byte[] data = new byte[1024 * 10];
            int count = 0;
            int total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                outputStream.write(data, 0, count);
                progressBar.setValue((int)(total * 100) / fileLength);
                sleep(5);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void done() {

        JOptionPane.showMessageDialog(null, "데이터 가져오기 잘수행했습니다.༼ つ ◕_◕ ༽つ");
        buttonDownload.setEnabled(true);
    }
}

