Gallery_Images = [].slice.call(document.querySelectorAll(".photo__section__image"));
Overlay = document.querySelector(".zoom__overlay");
Close_Button = document.querySelector(".zoom__overlay__close__btn");

Close_Button.addEventListener("click", () => {
    Overlay.style.display = 'none';
})

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
    if(login != undefined) {
        Enter_Link.textContent = login;
        document.querySelector('.enter__link__image').src = "../icon_logged.png";
        document.querySelector('.enter__overlay__unlogged').style.display = 'none';
        document.querySelector('.enter__overlay__logged').style.display = 'flex';
        document.querySelector('.enter__overlay__ok__button').addEventListener('click', () => {
            Enter_Overlay.style.display = 'none';
        });
        document.querySelector('.enter__overlay__unlog__button').addEventListener('click', () => {
            document.cookie = "login=hahaha;path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT";
            location.reload();
        });
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

function doAjax(method, value, callback, isAsync) {
	if (isAsync == undefined) {
		isAsync = true;
	}
	var http = new XMLHttpRequest();
	var data = "?qt=" + method + '&v=' + encodeURI(value);

	http.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var text = this.responseText;
			if (typeof callback == "function") {
				callback(text);
			} 
		}
	}
	http.open('GET', 'db.php' + data, isAsync);
	http.send();
}

document.querySelector(".add__photo__section__confirm__btn").addEventListener('click',()=>{
    let src = document.querySelector(".add__photo__section__url__input").value;
    let user = document.querySelector(".add__photo__user__input").value;
    let title = document.querySelector(".add__photo__title__input").value;
    let date = document.querySelector(".add__photo__date__input").value;
    let location = document.querySelector(".add__photo__location__input").value;
    if(src==''||user==''||title==''||date==''||location==''){
        alert("Все поля должны быть заполнены!");
        
    } else {
        img = {
            src: src,
            user : user,
            title: title,
            date : date,
            location: location
        };
        doAjax('add', JSON.stringify(img), (res)=>{
            setContent();
            for(let input of [].slice.call(document.querySelectorAll(".add__photo__section__input"))){
                input.value = "";
            }
            document.querySelector(".add__photo__section__url__input").value = "";
        }, true);
    }
    
});

function photoSection(image){
    let html = `<div class="photo__section">
                    <img class="photo__section__image" src="${image.src}">
                    <ul class="photo__info">
                        <li class="photo__info__line">
                            <img class="photo__info__icon" src="icons/icon_user.png">
                            <input class="photo__section__input photo__${image.id}__user__input photo__${image.id}__input" placeholder="Пользователь" value="${image.user}">
                        </li>
                        <li class="photo__info__line">
                            <img class="photo__info__icon" src="icons/icon_info.png">
                            <input class="photo__section__input photo__${image.id}__title__input photo__${image.id}__input" placeholder="Название" value="${image.title}">
                        </li>
                        <li class="photo__info__line">
                            <img class="photo__info__icon" src="icons/icon_time.png">
                            <input class="photo__section__input photo__${image.id}__date__input photo__${image.id}__input" type="date" placeholder="Дата" value="${image.date}">
                        </li>
                        <li class="photo__info__line">
                            <img class="photo__info__icon" src="icons/icon_location.png">
                            <input class="photo__section__input photo__${image.id}__location__input photo__${image.id}__input" placeholder="Место" value="${image.location}">
                        </li>
                        <li>
                            <div class="photo__section__button__div">
                                <button class="photo__section__edit__button photo__section__button" data-image_id="${image.id}">Изменить</button>
                                <button class="photo__section__delete__button photo__section__button" data-image_id="${image.id}">Удалить</button>
                            </div>
                        </li>
                    </ul>
                </div>`
    return html;
}

function setContent(){
    doAjax("all", "", (txt) => {
        let Content = document.querySelector(".content");
        Content.innerHTML = "";
        let images = JSON.parse(txt);
        for(let image of images){
            Content.innerHTML += photoSection(image);
        }
        for(let input of [].slice.call(document.querySelectorAll(".photo__section__input"))){
            input.style.pointerEvents = "none";
        }
        for(let delete_button of [].slice.call(document.querySelectorAll(".photo__section__delete__button"))){
            delete_button.addEventListener('click', ()=>{
                let id = delete_button.dataset.image_id;
                deleteImage(id);
            })
        }
        for(let edit_button of [].slice.call(document.querySelectorAll(".photo__section__edit__button"))){
            edit_button.addEventListener('click', ()=>{
                let id = edit_button.dataset.image_id;
                for(let input of [].slice.call(document.querySelectorAll(`.photo__${id}__input`))){
                    input.style.pointerEvents = "all";
                    input.style.border = "3px #000000 solid";
                }
                edit_button.textContent = "Сохранить";
                edit_button.addEventListener('click', ()=>{
                    let new_user = document.querySelector(`.photo__${id}__user__input`).value;
                    let new_title = document.querySelector(`.photo__${id}__title__input`).value;
                    let new_date = document.querySelector(`.photo__${id}__date__input`).value;
                    let new_location = document.querySelector(`.photo__${id}__location__input`).value;
                    if(new_user==''||new_title==''||new_date==''||new_location==''){
                        alert("Все поля должны быть заполнены!");
                    } else {
                        modified_image = {
                            id: id,
                            user: new_user,
                            title: new_title,
                            date: new_date,
                            location: new_location
                        }
                        doAjax("update", JSON.stringify(modified_image), (res)=>{setContent()}, true);
                    }
                    
                })
            })
        }
    }, true);
}

function deleteImage(id){
    doAjax("delete", id, (res)=>{setContent()}, true);
}

setContent();


