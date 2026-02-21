InputEmail = $('.input__email')[0];
InputPassword = $('.input__password')[0];
ButtonConfirm = $('.button__register')[0];
TextServerResponse = $('.text__server__respose')[0];

redirectIfValidCredentials('/all-products', null);

ButtonConfirm.addEventListener('click', () => {
    $('.text__server__respose').show();
    validateValues();
    if(ValuesAreValid){
        TextServerResponse.innerText = "Обрабатываем ваш запрос...";
        TextServerResponse.style.color = "gray";
        sendRegisterAjax();
    } else {
        TextServerResponse.innerText = "Заполните все поля!";
        TextServerResponse.style.color = "red";
    }
});

function sendRegisterAjax(){
    $.ajax({
        url:'api/login',
        method:'post',
        data: JSON.stringify({email: InputEmail.value, password: InputPassword.value}),
        contentType : 'application/json',
        success : ProcessServerResponse
    });
}

function validateValues() {
    ValuesAreValid = !(InputPassword.value.trim() === "" || InputEmail.value.trim() === "");
}

function ProcessServerResponse(response){
    if(response.error != null){
        TextServerResponse.innerText = response.error;
        TextServerResponse.style.color = "red";
    } else {
        setCookie('email', response.email, {secure: true, 'max-age': 3600});
        setCookie('password_hash', response.password, {secure: true, 'max-age': 3600});
        TextServerResponse.innerText = response.success;
        TextServerResponse.style.color = "green";
        window.location.href = "/all-products";
        InputPassword.value = "";
        InputEmail.value = "";
    }
}
