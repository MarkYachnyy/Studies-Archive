InputLogin = $('.input__login')[0];
InputPassword = $('.input__password')[0];
InputPasswordConfirm = $('.input__password__confirm')[0];
ButtonConfirm = $('.button__register')[0];
TextServerResponse = $('.text__server__respose')[0];
let ValuesAreValid = true;

ButtonConfirm.addEventListener('click', () => {
    $('.text__server__respose').show();
    validateValues();
    if(ValuesAreValid){
        TextServerResponse.innerText = "Обрабатываем ваш запрос...";
        TextServerResponse.style.color = "gray";
       setRegisterAjax();
    } else {
        TextServerResponse.style.color = "red";
        console.log("values are invalid");
    }
});

function setRegisterAjax(){
    $.ajax({
        url:'api/register_user',
        method:'post',
        data: JSON.stringify({name: InputLogin.value, password: InputPassword.value}),
        contentType : 'application/json',
        success : ProcessServerResponse
    });
}

function validateValues() {
    ValuesAreValid = true;
    if(InputLogin.value.length < 8 || InputLogin.value.length > 64){
        TextServerResponse.innerText = "Имя пользователя должно быть от 8 до 16 символов";
        ValuesAreValid = false;
    }
    if(InputPassword.value.length < 8 || InputPassword.value.length > 16){
        TextServerResponse.innerText = "Длина пароля должна быть от 8 до 16 символов";
        ValuesAreValid = false;
    }
    if(InputPassword.value !== InputPasswordConfirm.value){
        TextServerResponse.innerText = "Пароли не совпадают";
        ValuesAreValid = false;
    }
}

function ProcessServerResponse(response){
    if(response.success == null){
        TextServerResponse.innerText = response.error;
        TextServerResponse.style.color = "red";
    } else {
        TextServerResponse.innerText = response.success;
        TextServerResponse.style.color = "green";
        InputLogin.value = "";
        InputPassword.value = "";
        InputPasswordConfirm.value = "";
    }
}
