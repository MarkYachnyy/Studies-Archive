DivOtherSpendingParticipants = $(".div__other__spending__participants")[0];
DivOverlayFriendsList = $(".div__overlay__friends")[0];
InputOverallAmount = $(".input__overall__amount")[0];
ButtonConfirmNewSpending = $(".button__confirm__new__spending")[0];
InputNewSpendingName = $(".input__new__spending__name")[0];
InputNewSpendingDate = $(".input__new__spending__date")[0];
TextNewSpendingCreationStatus = $(".text__spending__creation__status")[0];
RadioButtonEqualParts = $("#radio-button-equal-parts")[0];
TextAddOutsiderStatus = $(".text__add__outsider__to__spending__status")[0];
ButtonAddOutsider = $(".button__add__outsider__to__spending")[0];
InputOutsiderName = $(".input__add__outsider__to__spending__name")[0];

FriendsNotAddedToSpendingList = [];
FriendsAddedToSpendingList = [];
OutsidersAddedToSpendingList = [];
PayerName = Username;

$.getJSON("api/user/current", null, user => {
    Username = user.name;
    UserId = user.id;
    PayerName = Username;
    $(".button__create__new__spending")[0].addEventListener("click", () => {
        $(".overlay__create__new__payment")[0].style.display = "flex";
        setOverlayAddedFriendListHTML();
    });
});

RadioButtonEqualParts.click();

$.getJSON("api/friends/all", null, friends_list => {
    res = "";
    for (let friend of friends_list) {
        FriendsNotAddedToSpendingList.push(friend.name);
    }
    setOverlayNonAddedFriendListHTML();
    setOverlayAddedFriendListHTML();
});

InputOverallAmount.addEventListener("change", () => {
    if (!isNaN(InputOverallAmount.value)) {
        setOverlayAddedFriendListHTML();
    }
});

function setOverlayNonAddedFriendListHTML() {
    DivOverlayFriendsList.innerHTML = "";
    for (let name of FriendsNotAddedToSpendingList) {
        let btn = document.createElement("button");
        btn.addEventListener("click", () => {
            FriendsAddedToSpendingList.push(name);
            let index = FriendsNotAddedToSpendingList.indexOf(name);
            FriendsNotAddedToSpendingList.splice(index, 1);
            setOverlayAddedFriendListHTML();
            setOverlayNonAddedFriendListHTML();
        });
        btn.style.width = "40px";
        btn.style.height = "40px";
        btn.innerText = "+";

        let div = document.createElement("div");
        div.style.display = "flex";
        div.style.flexDirection = "row";
        div.style.justifyContent = "space-between";
        div.style.margin = "5px";
        div.style.padding = "10px";
        div.style.background = "#EEEEEE";
        div.append(btn);

        let p = document.createElement("p");
        p.innerText = name;
        div.append(p);
        DivOverlayFriendsList.append(div);
    }
}

function setOverlayAddedFriendListHTML() {
    let equalPart = Math.floor(Number(InputOverallAmount.value) / (FriendsAddedToSpendingList.length + OutsidersAddedToSpendingList.length + 1));

    DivOtherSpendingParticipants.innerHTML = "";

    let div_us = createParticipantDiv();

    let img_creator = document.createElement("img");
    img_creator.style.width = "40px";
    img_creator.style.height = "40px";
    img_creator.src = "icon/wrench.png";
    div_us.append(img_creator);

    let p_us = document.createElement("button");
    p_us.innerText = Username;
    p_us.style.height = "40px";
    p_us.addEventListener("click", () => {
        PayerName = Username;
        setOverlayAddedFriendListHTML();
    });

    div_us.append(p_us);
    div_us.append(createPersonAmountElement(Username, equalPart));

    DivOtherSpendingParticipants.append(div_us)

    for (let name of FriendsAddedToSpendingList) {
        let btn = document.createElement("button");
        btn.addEventListener("click", () => {
            FriendsNotAddedToSpendingList.push(name);
            let index = FriendsAddedToSpendingList.indexOf(name);
            FriendsAddedToSpendingList.splice(index, 1);
            if (PayerName === name) {
                PayerName = Username;
            }
            setOverlayAddedFriendListHTML();
            setOverlayNonAddedFriendListHTML();
        });
        btn.style.width = "40px";
        btn.style.height = "40px";
        btn.innerText = "-";

        let div = createParticipantDiv();
        div.append(btn);

        let p = document.createElement("button");
        p.innerText = name;
        p.style.height = "40px";
        p.addEventListener("click", () => {
            PayerName = name;
            setOverlayAddedFriendListHTML();
        });
        div.append(p);
        div.append(createPersonAmountElement(name, equalPart));

        DivOtherSpendingParticipants.append(div);
    }

    for (let name of OutsidersAddedToSpendingList) {
        let btn = document.createElement("button");
        btn.addEventListener("click", () => {
            let index = OutsidersAddedToSpendingList.indexOf(name);
            OutsidersAddedToSpendingList.splice(index, 1);
            if (PayerName === name) {
                PayerName = Username;
            }
            setOverlayAddedFriendListHTML();
            setOverlayNonAddedFriendListHTML();
        });
        btn.style.width = "40px";
        btn.style.height = "40px";
        btn.innerText = "-";

        let div = createParticipantDiv();
        div.append(btn);

        let p = document.createElement("button");
        p.innerText = name;
        p.style.height = "40px";
        p.addEventListener("click", () => {
            PayerName = name;
            setOverlayAddedFriendListHTML();
        });
        div.append(p);
        div.append(createPersonAmountElement(name, equalPart));

        DivOtherSpendingParticipants.append(div);
    }
    checkCurrentDistributionMethod();
}

function createParticipantDiv() {
    let div = document.createElement("div");
    div.style.display = "flex";
    div.style.flexDirection = "row";
    div.style.justifyContent = "space-between";
    div.style.margin = "5px";
    div.style.padding = "10px";
    div.style.background = "#EEEEEE";
    return div;
}

function createPersonAmountElement(name, equalPart) {
    let el;
    if (name === PayerName) {
        el = document.createElement("img");
        el.src = "icon/crown.png";
        el.style.width = "40px";
        el.style.height = "40px";
    } else {
        el = document.createElement("div");
        el.style.display = "flex";
        el.style.alignItems = "center";
        el.innerHTML = `<input class="input__person__explicit__amount" id="input-${name}-explicit-amount" style="width: 50px;">
        <p class="text__equal__part">${equalPart}</p>
        <p> ₽</p>`
    }
    return el;
}

ButtonConfirmNewSpending.addEventListener("click", () => {
    if (InputNewSpendingName.value.toString().trim() === "") {
        TextNewSpendingCreationStatus.style.color = "red";
        TextNewSpendingCreationStatus.innerText = "Заполните поле ввода названия траты";
        return;
    }
    if (InputNewSpendingDate.value === "") {
        TextNewSpendingCreationStatus.style.color = "red";
        TextNewSpendingCreationStatus.innerText = "Заполните поле ввода даты траты";
        return;
    }
    if (FriendsAddedToSpendingList.length + OutsidersAddedToSpendingList.length === 0) {
        TextNewSpendingCreationStatus.style.color = "red";
        TextNewSpendingCreationStatus.innerText = "Добавьте в счёт хотя бы одного человека";
        return;
    }
    if (CurrentMethodValue === RadioButtonEqualParts.value && (isNaN(Number(InputOverallAmount.value)) || Number(InputOverallAmount.value) <= 0)) {
        TextNewSpendingCreationStatus.style.color = "red";
        TextNewSpendingCreationStatus.innerText = "Введите корректное значение суммы счёта (положительное число)";
        return;
    }
    if (CurrentMethodValue === $("#radio-button-explicit-parts")[0].value) {
        let valid = true;
        for (let input of [...document.querySelectorAll(".input__person__explicit__amount")]) {
            valid = valid && (!isNaN(Number(input.value)) && Number(input.value) > 0);
        }
        if (!valid) {
            TextNewSpendingCreationStatus.style.color = "red";
            TextNewSpendingCreationStatus.innerText = "Некоторые значения долей людей некорректны";
            return;
        }
    }
    let res = {};
    res.name = InputNewSpendingName.value;
    res.date = InputNewSpendingDate.value
    res.creatorName = Username;
    res.payerName = PayerName;
    res.debts = {};
    let equalPart = Math.floor(Number(InputOverallAmount.value) / (FriendsAddedToSpendingList.length + OutsidersAddedToSpendingList.length + 1));

    if (CurrentMethodValue === RadioButtonEqualParts.value) {
        for (let name of FriendsAddedToSpendingList) {
            if (name === PayerName) {
                res.debts[name] = 0;
            } else {
                res.debts[name] = equalPart;
            }
        }
        for (let name of OutsidersAddedToSpendingList) {
            if (name === PayerName) {
                res.debts[name] = 0;
            } else {
                res.debts[name] = equalPart;
            }
        }
        if (Username === PayerName) {
            res.debts[Username] = 0;
        } else {
            res.debts[Username] = equalPart;
        }
    } else {
        for (let name of FriendsAddedToSpendingList) {
            if (name === PayerName) {
                res.debts[name] = 0;
            } else {
                res.debts[name] = Number($(`#input-${name}-explicit-amount`)[0].value);
            }
        }
        for (let name of OutsidersAddedToSpendingList) {
            if (name === PayerName) {
                res.debts[name] = 0;
            } else {
                res.debts[name] = Number($(`#input-${name}-explicit-amount`)[0].value);
            }
        }
        if (Username === PayerName) {
            res.debts[Username] = 0;
        } else {
            res.debts[Username] = Number($(`#input-${Username}-explicit-amount`)[0].value);
        }
    }
    for (let name of OutsidersAddedToSpendingList) {
        $.post(`api/friends/send-request/${name}`, null, response => {

        });
    }

    $.ajax({
        url: 'api/spendings/new',
        method: 'post',
        data: JSON.stringify(res),
        contentType: 'application/json',
        success: response => {
            if (response.success == null) {
                TextNewSpendingCreationStatus.innerText = response.error;
                TextNewSpendingCreationStatus.style.color = "red";
            } else {
                TextNewSpendingCreationStatus.innerText = response.success;
                TextNewSpendingCreationStatus.style.color = "green";
                location.reload();
            }
        }
    });
});

InputNewSpendingDate.value = new Date().toISOString().substring(0, 10);

$(".button__close__create__new__payment__overlay")[0].addEventListener("click", () => {
    $(".overlay__create__new__payment")[0].style.display = "none";
});

ButtonAddOutsider.addEventListener("click", () => {
    let name = InputOutsiderName.value.trim();
    if (name === "") {
        TextAddOutsiderStatus.style.color = "red";
        TextAddOutsiderStatus.innerText = "Заполните поле";
    } else if(name === Username){
        TextAddOutsiderStatus.style.color = "red";
        TextAddOutsiderStatus.innerText = "Это ваш ник!";
    } else if (FriendsNotAddedToSpendingList.indexOf(name) >= 0) {
        TextAddOutsiderStatus.style.color = "green";
        TextAddOutsiderStatus.innerText = "Пользователь добавлен";

        let index = FriendsNotAddedToSpendingList.indexOf(name);
        FriendsNotAddedToSpendingList.splice(index, 1);

        FriendsAddedToSpendingList.push(name);
        setOverlayNonAddedFriendListHTML();
        setOverlayAddedFriendListHTML();
    } else if (FriendsAddedToSpendingList.indexOf(name) >= 0 || OutsidersAddedToSpendingList.indexOf(name) >= 0) {
        TextAddOutsiderStatus.style.color = "red";
        TextAddOutsiderStatus.innerText = "Этот пользователь уже есть в счёте";
    } else {
        TextAddOutsiderStatus.style.color = "gray";
        TextAddOutsiderStatus.innerText = "Запрос обрабатывается...";
        $.get({
            url: `api/user/exists`,
            data: {'name': name},
            success: response => {
                let exists = Boolean(response);
                if(exists){
                    OutsidersAddedToSpendingList.push(name);
                    setOverlayAddedFriendListHTML();
                    TextAddOutsiderStatus.style.color = "green";
                    TextAddOutsiderStatus.innerText = "Пользователь добавлен";
                } else {
                    TextAddOutsiderStatus.style.color = "red";
                    TextAddOutsiderStatus.innerText = "Пользователя с таким именем не существует";
                }
            }
        });
    }
})

