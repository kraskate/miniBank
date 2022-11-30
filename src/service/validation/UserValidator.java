package service.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {

    private static UserValidator instance;

    private UserValidator() {
    }

    public static UserValidator getInstance() {
        if (instance == null) {
            instance = new UserValidator();
        }
        return instance;
    }

    public UserValidationResult validate(UserValidationRequest request) {

        String userNameRegexp = "^\\w{4,8}$";
        Pattern userNamePattern = Pattern.compile(userNameRegexp);
        String userNameInvalid = "Неподходящее имя пользователя. Должно быть одним словом из английских букв и цифр длиной от 4 до 8 символов";

        String passwordRegexp = "^[0-9]{6,10}$";
        Pattern passwordPattern = Pattern.compile(passwordRegexp);
        String passwordInvalid = "Неподходящий пароль. Должен быть из цифер длиной от 6 до 10 символов";

        String nameRegexp = "^[a-zA-Z]+$";
        Pattern namePattern = Pattern.compile(nameRegexp);
        String nameInvalid = "Неподходящее имя или фамилия. Должно быть одним словом только из английских букв любого региcтра больше 1 символа длиной";

        String birthDateInvalid = "Неподходящая дата рождения. Должно быть гггг-мм-дд, например 2022-12-24";

        String sexRegexp = "^[M,W]$";
        Pattern sexPattern = Pattern.compile(sexRegexp);
        String sexInvalid = "Неподходящий пол. M или W допустимы";

        String emailRegexp = "^\\w+@\\w+\\.\\w+$";
        Pattern emailPattern = Pattern.compile(emailRegexp);
        String emailInvalid = "Неподходящий email. Должен содержать \"@\" и \".\" после нее, а также буквы между ними";

        UserValidationResult result = new UserValidationResult();
        if (!userNamePattern.matcher(request.getUserName()).matches()) {
            result.addError(userNameInvalid);
        }
        if (!passwordPattern.matcher(request.getPassword()).matches()) {
            result.addError(passwordInvalid);
        }
        if (!namePattern.matcher(request.getFirstName()).matches()) {
            result.addError(nameInvalid);
        }
        if (!namePattern.matcher(request.getLastName()).matches()) {
            result.addError(nameInvalid);
        }

        try {
            LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            result.addError(birthDateInvalid);
        }

        if (!sexPattern.matcher(request.getSex()).matches()) {
            result.addError(sexInvalid);
        }
        if (!emailPattern.matcher(request.getEmail()).matches()) {
            result.addError(emailInvalid);
        }

        return result;
    }
}
