package mapper;

import model.Sex;
import model.User;
import service.validation.UserValidationRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class UserMapper {

    public static String toCsv(User user) {

        CsvBuilder builder = new CsvBuilder();
        return builder.add(user.getId())
                .add(user.getUserName())
                .add(user.getPassword())
                .add(user.getFirstName())
                .add(user.getLastName())
                .add(user.getBirthDate())
                .add(user.getSex())
                .addAndBuild(user.getEmail());


    }

    public static User toObject(String csv) {
        String[] strings = csv.split(",");
        int i = 0;

        User user = new User();
        user.setId(java.lang.Long.parseLong(strings[i++]));
        user.setUserName(strings[i++]);
        user.setPassword(strings[i++]);
        user.setFirstName(strings[i++]);
        user.setLastName(strings[i++]);
        user.setBirthDate(LocalDate.parse(strings[i++], DateTimeFormatter.ISO_LOCAL_DATE));
        user.setSex(Sex.valueOf(strings[i++]));
        user.setEmail(strings[i++]);

        return user;

    }

    public static User toObject(UserValidationRequest request) {
        return new User(request.getUserName(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                LocalDate.parse(request.getBirthDate()),
                Sex.valueOf(request.getSex()),
                request.getEmail(),
                Collections.emptyList());
    }
}
