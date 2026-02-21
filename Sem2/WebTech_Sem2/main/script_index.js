function getCookie(cookieName) {
    let cookie = {};
    document.cookie.split(';').forEach(function(el) {
      let [key,value] = el.split('=');
      cookie[key.trim()] = value;
    })
    return cookie[cookieName];
}
function checkEnter(){
    let login = getCookie('login');
    if(login != undefined){
        Enter_Link.textContent = login;
        document.querySelector('.enter__link__image').src = "../icon_logged.png";
        document.querySelector('.enter__overlay__unlogged').style.display = 'none';
        document.querySelector('.enter__overlay__logged').style.display = 'flex';
        document.querySelector('.enter__overlay__ok__button').addEventListener('click', () => {
            Enter_Overlay.style.display = 'none';
        })
        document.querySelector('.enter__overlay__unlog__button').addEventListener('click', () => {
            document.cookie = "login=hahaha;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT";
            location.reload();
        })
        document.querySelector('.enter__overlay__username__span').textContent = getCookie('login');
    }
}

Enter_Overlay = document.querySelector(".enter__overlay");
Enter_Link = document.querySelector(".enter__link");
Cancel_Btn = document.querySelector(".enter__overlay__btn__cancel");
Enter_Btn = document.querySelector(".enter__overlay__btn__enter");
Enter_Link.addEventListener("click", () => {
    Enter_Overlay.style.display = 'flex';
})
Cancel_Btn.addEventListener("click", () => {
    Enter_Overlay.style.display = 'none';
})
Enter_Btn.addEventListener("click", () => {
    let Password_Input = document.querySelector(".enter__password__input");
    let Login_Input = document.querySelector(".enter__login__input");
    if(Password_Input.value != '' && Login_Input.value != ''){
        document.cookie = "login=" + Login_Input.value +";path=/";
        location.reload();
    }
})

checkEnter();