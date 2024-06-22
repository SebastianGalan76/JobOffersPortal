package com.coresaken.jobportal.service;

import com.coresaken.jobportal.data.dto.ApplicationDto;
import com.coresaken.jobportal.data.dto.HelpMessageDto;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSenderService {
    @Autowired
    JavaMailSender mailSender;

    @Value("${website.address}")
    String websiteAddress;

    public void sendActiveAccountEmail(String to, String activeAccountToken) throws MessagingException{
        String activeAccountLink = websiteAddress+"auth/active/"+activeAccountToken;

        String activeAccountEmail = "<html>"
                + "<body style='margin: 0; padding: 0; font-size: 1.2em; background-color: #e9e9e9; font-family: system-ui, -apple-system, BlinkMacSystemFont, \'Segoe UI\', Roboto, Oxygen, Ubuntu, Cantarell, \'Open Sans\', \'Helvetica Neue\', sans-serif;'>"
                + "    <div id='header' style='background-color: #1f1f7c; width: 100%; height: 250px;'></div>"
                + "    <div id='panel' style='background-color: white; width: 700px; margin-left: auto; margin-right: auto; margin-top: -100px; border-radius: 5px;'>"
                + "        <div id='logo' style='width: 250px; padding: 25px; margin-left: auto; margin-right: auto;'>"
                + "            <img src='https://zapodaj.net/images/fb31beff19dd9.png' style='width: 100%; height: auto;'>"
                + "        </div>"
                + "        <div id='container' style='padding: 25px'>"
                + "            Dziękujemy za zarejestrowanie się na <span style='font-weight: 800;'>JoinGameDev.pl!</span><br><br><br>"
                + "            Aby zakończyć proces rejestracji i aktywować swoje konto, prosimy o kliknięcie poniższy przycisk:"
                + "            <a href='"+activeAccountLink+"' style='text-decoration: none;'>"
                + "                <div id='button' style='background-color: #ffbe4d; padding: 10px 50px; width: fit-content; border-radius: 5px; color: white; font-weight: 800; margin-top: 20px; margin-bottom: 30px; margin-left: auto; margin-right: auto;'>"
                + "                    Aktywuj konto"
                + "                </div>"
                + "            </a>"
                + "            Jeśli przycisk nie działa, przejdź na poniższy adres URL:<br>"
                + "            <span style='color: #ffbe4d'><a href='"+activeAccountLink+"'>"+activeAccountLink+"</a></span>"
                + "            <br><br><br>Jeśli nie rejestrowałeś/aś się na naszej stronie, zignoruj tę wiadomość."
                + "        </div>"
                + "    </div>"
                + "    <div id='panel-down' style='background-color: #d1d1d1; padding: 25px 50px; width: 600px; margin-left: auto; margin-right: auto; margin-top: 50px; margin-bottom: 150px; text-align: center;'>"
                + "        Jeśli potrzebujesz pomocy, prosimy o kontakt <br>"
                + "        <span style='font-weight: 800;'>support@joingamedev.pl</span>"
                + "    </div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom("JoinGameDev@joingamedev.pl");
        helper.setSubject("JoinGameDev.pl - Aktywacja konta użytkownika");
        helper.setText(activeAccountEmail, true);

        mailSender.send(message);
    }
    public void sendResetPasswordEmail(String to, String token) throws MessagingException {
        String resetPasswordLink = websiteAddress+"auth/resetPassword?token=" + token;

        String resetPasswordEmail = "<html>"
                + "<body style='margin: 0; padding: 0; font-size: 1.2em; background-color: #e9e9e9; font-family: system-ui, -apple-system, BlinkMacSystemFont, \'Segoe UI\', Roboto, Oxygen, Ubuntu, Cantarell, \'Open Sans\', \'Helvetica Neue\', sans-serif;'>"
                + "    <div id='header' style='background-color: #1f1f7c; width: 100%; height: 250px;'></div>"
                + "    <div id='panel' style='background-color: white; width: 700px; margin-left: auto; margin-right: auto; margin-top: -100px; border-radius: 5px;'>"
                + "        <div id='logo' style='width: 250px; padding: 25px; margin-left: auto; margin-right: auto;'>"
                + "            <img src='https://zapodaj.net/images/fb31beff19dd9.png' style='width: 100%; height: auto;'>"
                + "        </div>"
                + "        <div id='container' style='padding: 25px'>"
                + "            Otrzymaliśmy prośbę o zresetowanie hasła do Twojego konta na stronie <span style='font-weight: 800;'>joingamedev.pl!</span><br><br><br>"
                + "            Jeśli chcesz zresetować hasło, kliknij poniższy przycisk:"
                + "            <a href='"+resetPasswordLink+"' style='text-decoration: none;'>"
                + "                <div id='button' style='background-color: #ffbe4d; padding: 10px 50px; width: fit-content; border-radius: 5px; color: white; font-weight: 800; margin-top: 20px; margin-bottom: 30px; margin-left: auto; margin-right: auto;'>"
                + "                    Zresetuj hasło"
                + "                </div>"
                + "            </a>"
                + "            Jeśli przycisk nie działa, przejdź na poniższy adres URL:<br>"
                + "            <span style='color: #ffbe4d'><a href='"+resetPasswordLink+"'>"+resetPasswordLink+"</a></span>"
                + "            <br><br><br>Jeśli nie resetowałeś hasła na naszej stronie, zignoruj tę wiadomość."
                + "        </div>"
                + "    </div>"
                + "    <div id='panel-down' style='background-color: #d1d1d1; padding: 25px 50px; width: 600px; margin-left: auto; margin-right: auto; margin-top: 50px; margin-bottom: 150px; text-align: center;'>"
                + "        Jeśli potrzebujesz pomocy, prosimy o kontakt <br>"
                + "        <span style='font-weight: 800;'>support@joingamedev.pl</span>"
                + "    </div>"
                + "</body>"
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom("JoinGameDev@joingamedev.pl");
        helper.setSubject("JoinGameDev.pl - Resetowanie hasła");
        helper.setText(resetPasswordEmail, true);

        mailSender.send(message);
    }

    public void sendApplication(ApplicationDto applicationDto, JobOffer jobOffer) throws MessagingException {
        String emailContent = "<html lang='pl'>" +
                "<body style='margin: 0; padding: 0; font-size: 1.2em; font-family: system-ui, -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen, Ubuntu, Cantarell, \"Open Sans\", \"Helvetica Neue\", sans-serif;'>" +
                "    <div id='panel'" +
                "        style='background-color: rgb(243, 243, 243); width: 700px; margin-left: auto; margin-right: auto; margin-top: 20px; padding: 10px 20px 10px 20px;'>" +
                "        <div id='up-panel'" +
                "            <span style='font-weight: 800; display: block; font-size: 1.1em; color: #363636;'>"+jobOffer.getTitle()+"</span>" +
                "            <a href='"+websiteAddress+"offer/"+jobOffer.getLinkUrl()+"'><span style='display: block; background-color: #4e8bfd; width: fit-content; padding: 3px 10px; border-radius: 5px; font-weight: 500; color: rgb(235, 235, 235); cursor: pointer; margin-bottom: 5px; margin-top: 5px;'>Oferta</span></a>" +
                "        </div>" +
                "        <div id='down-panel' style='display: flex; flex-direction: column; border-top: 1px solid #d1d1d1; padding: 10px;'>" +
                "            <span style='font-weight: 700;'>"+applicationDto.getFirstName() +" "+applicationDto.getLastName()+"</span><br>" +
                "            <span>"+applicationDto.getUserEmail()+"</span>" +
                "        </div>" +
                "    </div>" +
                "    <div id='container' style='padding: 25px'>" +
                applicationDto.getMessage() +
                "    </div>" +
                "    <div id='panel-down' style='background-color: rgb(243, 243, 243); padding: 25px 50px; width: 600px; margin-left: auto; margin-right: auto; margin-top: 50px; margin-bottom: 150px; text-align: center;'>" +
                "        Jeśli odpowiesz na ten e-mail, to zostanie on przesłany do kandydata.<br>" +
                "        <span style='font-weight: 800;'>"+applicationDto.getUserEmail()+"</span>" +
                "    </div>" +
                "</body>" +
                "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String title = jobOffer.getAction().getTitle();
        title = title.replace("{firstName}", applicationDto.getFirstName()).replace("{lastName}", applicationDto.getLastName());
        helper.setTo(jobOffer.getAction().getEmail());
        helper.setFrom("JoinGameDev@joingamedev.pl");
        helper.setSubject(title);
        helper.setText(emailContent, true);
        helper.setReplyTo(applicationDto.getUserEmail());

        if (applicationDto.getFilePaths() != null) {
            for (String filePath : applicationDto.getFilePaths()) {
                File file = new File(filePath);
                if (file.exists()) {
                    FileSystemResource fileResource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), fileResource);
                } else {
                    System.err.println("Plik nie został znaleziony: " + filePath);
                }
            }
        }

        mailSender.send(message);

        sendApplicationConfirmation(applicationDto, jobOffer);
    }
    private void sendApplicationConfirmation(ApplicationDto applicationDto, JobOffer jobOffer) throws MessagingException {
        String emailContent = "<html lang='pl'>" +
                "<body style='margin: 0; padding: 0; font-size: 1.2em; font-family: system-ui, -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen, Ubuntu, Cantarell, \"Open Sans\", \"Helvetica Neue\", sans-serif;'>" +
                "<div style='width: 600px;background-color: rgb(145, 255, 141); padding: 20px; margin-bottom: 20px; margin-left: auto; margin-right: auto; font-weight: 700;'>"+
                "Zgłoszenie zostało przesłane do pracodawcy"+
                "</div>"+
                "    <div id='panel'" +
                "        style='background-color: rgb(243, 243, 243); width: 700px; margin-left: auto; margin-right: auto; margin-top: 20px; padding: 10px 20px 10px 20px;'>" +
                "        <div id='up-panel'" +
                "            <span style='font-weight: 800; display: block; font-size: 1.1em; color: #363636;'>"+jobOffer.getTitle()+"</span>" +
                "            <a href='"+websiteAddress+"offer/"+jobOffer.getLinkUrl()+"'><span style='display: block; background-color: #4e8bfd; width: fit-content; padding: 3px 10px; border-radius: 5px; font-weight: 500; color: rgb(235, 235, 235); cursor: pointer; margin-bottom: 5px; margin-top: 5px;'>Oferta</span></a>" +
                "        </div>" +
                "        <div id='down-panel' style='display: flex; flex-direction: column; border-top: 1px solid #d1d1d1; padding: 10px;'>" +
                "            <span style='font-weight: 700;'>"+applicationDto.getFirstName() +" "+applicationDto.getLastName()+"</span><br>" +
                "            <span>"+applicationDto.getUserEmail()+"</span>" +
                "        </div>" +
                "    </div>" +
                "    <div id='container' style='padding: 25px'>" +
                applicationDto.getMessage() +
                "    </div>" +
                "    <div id='panel-down' style='background-color: rgb(243, 243, 243); padding: 25px 50px; width: 600px; margin-left: auto; margin-right: auto; margin-top: 50px; margin-bottom: 150px; text-align: center;'>" +
                "        Nie odpowiadaj na ten e-mail. Jeśli chcesz ponownie skontaktować się z pracodawcą, to wyślij ponownie swoją aplikację." +
                "    </div>" +
                "</body>" +
                "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(applicationDto.getUserEmail());
        helper.setFrom("JoinGameDev@joingamedev.pl");
        helper.setSubject("JoinGameDev.pl | Dostarczenie aplikacji do pracodawcy");
        helper.setText(emailContent, true);

        List<File> filesToDelete = new ArrayList<>();
        if (applicationDto.getFilePaths() != null) {
            for (String filePath : applicationDto.getFilePaths()) {
                File file = new File(filePath);
                if (file.exists()) {
                    FileSystemResource fileResource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), fileResource);
                    filesToDelete.add(file);
                } else {
                    System.err.println("Plik nie został znaleziony: " + filePath);
                }
            }
        }

        mailSender.send(message);
        for (File file : filesToDelete) {
            if (!file.delete()) {
                System.err.println("Nie udało się usunąć pliku: " + file.getAbsolutePath());
            }
        }
    }

    public void sendHelpMessage(HelpMessageDto helpMessageDto) throws MessagingException {
        String helpMessage = "<html>"+ helpMessageDto.getMessage()
                + "</html>";

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("support@joingamedev.pl");
        helper.setFrom("JoinGameDev@joingamedev.pl");
        helper.setReplyTo(helpMessageDto.getEmail());
        helper.setSubject("Pomoc - JoinGameDev.pl");
        helper.setText(helpMessage, true);

        mailSender.send(message);
    }
}
