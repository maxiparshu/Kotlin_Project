#include <string>
#include <vector>
#include <jni.h>
struct ReviewData {
    std::string text;
    std::string rest;
    std::string name;

    ReviewData() : text(""), rest(""), name("") {}
};

struct RestaurantInfo {
    std::string rest;
    std::string name;


    RestaurantInfo() : rest(""), name("") {}
};

// Определение структуры UserData
struct User {
    std::string id;
    bool isAdmin;
    std::string login;
    std::string password;
    std::vector<ReviewData> reviews;
    std::vector<RestaurantInfo> favouriteRest;


    User() : id(""), isAdmin(false), login(""), password("") {}
};
User* findUserByLogin(const std::vector<User>& users, const std::string& login) {
    for (const auto& user : users) {
        if (user.login == login) {
            return const_cast<User*>(&user);
        }
    }
    return nullptr; // Пользователь не найден
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_restoranapplication_LoginActivity_checkLogin(
        JNIEnv* env,
        jobject /* this */,
        jstring jLogin,
        jstring jPassword,
        jboolean jIsAdminChecked,
        jobject usersList) {

    const char* login = env->GetStringUTFChars(jLogin, nullptr);
    const char* password = env->GetStringUTFChars(jPassword, nullptr);

    // Конвертируем в std::string
    std::string loginStr(login);
    std::string passwordStr(password);

    std::vector<User> userList; // Создаем вектор для пользователей

    // Получаем класс List и методы для работы с ним
    jclass listClass = env->GetObjectClass(usersList);
    jmethodID sizeMethod = env->GetMethodID(listClass, "size", "()I");
    jmethodID getMethod = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");

    // Получаем размер списка пользователей
    jint userCount = env->CallIntMethod(usersList, sizeMethod);

    // Ищем пользователя в переданном списке
    for (jint i = 0; i < userCount; ++i) {
        jobject userObject = env->CallObjectMethod(usersList, getMethod, i);

        // Получаем класс UserData
        jclass userDataClass = env->GetObjectClass(userObject);

        // Получаем поле login
        jfieldID loginField = env->GetFieldID(userDataClass, "login", "Ljava/lang/String;");
        auto jUserLogin = (jstring)env->GetObjectField(userObject, loginField);
        const char* userLogin = env->GetStringUTFChars(jUserLogin, nullptr);

        jfieldID idField = env->GetFieldID(userDataClass, "id", "Ljava/lang/String;");
        auto jUserId = (jstring)env->GetObjectField(userObject, idField);
        const char* userId = env->GetStringUTFChars(jUserId, nullptr);
        // Получаем поле password

        jfieldID passwordField = env->GetFieldID(userDataClass, "password", "Ljava/lang/String;");
        auto jUserPassword = (jstring)env->GetObjectField(userObject, passwordField);
        const char* userPassword = env->GetStringUTFChars(jUserPassword, nullptr);

        // Получаем поле isAdmin
        jfieldID isAdminField = env->GetFieldID(userDataClass, "isAdmin", "Z");
        jboolean isAdmin = env->GetBooleanField(userObject, isAdminField);

        // Создаем объект UserData и добавляем его в вектор
        User userData;
        userData.login = userLogin ? userLogin : "";
        userData.password = userPassword ? userPassword : "";
        userData.isAdmin = (isAdmin == JNI_TRUE);
        userData.id = userId;

        // Добавляем созданного пользователя в вектор
        userList.push_back(userData);

        // Освобождаем временные строки
        env->ReleaseStringUTFChars(jUserLogin, userLogin);
        env->ReleaseStringUTFChars(jUserPassword, userPassword);
        env->DeleteLocalRef(userObject);
    }
    // Поиск пользователя
    User* tempUser = findUserByLogin(userList, loginStr);

    // Проверки
    if (loginStr.empty()) {
        env->ReleaseStringUTFChars(jLogin, login);
        env->ReleaseStringUTFChars(jPassword, password);
        return env->NewStringUTF("Введите логин");
    }
    if (passwordStr.empty()) {
        env->ReleaseStringUTFChars(jLogin, login);
        env->ReleaseStringUTFChars(jPassword, password);
        return env->NewStringUTF("Введите пароль");
    }
    if (tempUser == nullptr) {
        env->ReleaseStringUTFChars(jLogin, login);
        env->ReleaseStringUTFChars(jPassword, password);
        return env->NewStringUTF(("Аккаунт с логином " + loginStr + " не существует").c_str());
    }
    if (tempUser->password != passwordStr) {
        env->ReleaseStringUTFChars(jLogin, login);
        env->ReleaseStringUTFChars(jPassword, password);
        return env->NewStringUTF("Введен неправильный пароль");
    }
    if (tempUser->isAdmin != (jIsAdminChecked == JNI_TRUE)) {
        std::string errorMsg;
        if (!tempUser->isAdmin) {
            errorMsg = "Данный аккаунт зарегистрирован как обычный пользователь";
        } else {
            errorMsg = "Данный аккаунт зарегистрирован как администратор";
        }
        env->ReleaseStringUTFChars(jLogin, login);
        env->ReleaseStringUTFChars(jPassword, password);
        return env->NewStringUTF(errorMsg.c_str());
    }

    // Если все проверки пройдены
    std::string successMessage = "S" + tempUser->id;
    env->ReleaseStringUTFChars(jLogin, login);
    env->ReleaseStringUTFChars(jPassword, password);
    return env->NewStringUTF(successMessage.c_str());
}