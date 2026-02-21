DivAllSpendings = $(".div__all__spendings")[0];
SpanLoadingSpendingsMessage = $(".span__spendings__loading__message")[0];
DivAllDebts = $(".div__all__debts")[0];
SpanTotalDebtAmount = $(".span__total__debt__amount")[0];
CheckBoxHideZeroDebtSpendings = $("#input-check-hide-zero-debt-spendings")[0];
TextSpendingsAmount = $(".text__spending__count")[0];
TextDebtAmount = $(".text__debt__count")[0];
TextSpendingsCurrentPage = $(".text__spendings__current__page")[0];
ButtonSpendingsPreviousPage = $(".button__spendings__previous__page")[0];
ButtonSpendingsNextPage = $(".button__spendings__next__page")[0];

ItemsOnPage = 4;
CurrentPage = 1;
PageCount = 0;

Username = null;
UserId = null;
var SpendingList = null;

ButtonSpendingsNextPage.addEventListener("click" ,() => {
    if(CurrentPage < PageCount){
        CurrentPage++;
        loadCurrentPageOfSpendings();
    }
});

ButtonSpendingsPreviousPage.addEventListener("click", () => {
    if(CurrentPage > 1){
        CurrentPage--;
        loadCurrentPageOfSpendings();
    }
})

$.getJSON("api/user/current", null, user => {
    $(".span__username")[0].innerText = user.name;
    Username = user.name;
    UserId = user.id;
    $("#input-sort-criteria-date-descend")[0].click();
});

var CurrentComparator = null;
var ComparatorMap = new Map();
ComparatorMap.set($("#input-sort-criteria-debt-ascend")[0].value, "debtasc");
ComparatorMap.set($("#input-sort-criteria-debt-descend")[0].value, "debtdesc");
ComparatorMap.set($("#input-sort-criteria-date-descend")[0].value, "datedesc");
ComparatorMap.set($("#input-sort-criteria-date-ascend")[0].value, "debtasc");
document.querySelectorAll(".input__radio__sort__criteria").forEach(input => {
    input.addEventListener("click", () => {
        CurrentComparator = ComparatorMap.get(input.value);
        loadCurrentPageOfSpendings();
        loadDebts();
    });
});

CheckBoxHideZeroDebtSpendings.addEventListener("click", () => setSpendingsListHTML(SpendingList));

function loadCurrentPageOfSpendings() {
    SpanLoadingSpendingsMessage.style.display = "inline";
    $.getJSON(`api/spendings/part/${ItemsOnPage*(CurrentPage-1)}-${ItemsOnPage*CurrentPage}-${CurrentComparator}`, null, spendingList => {
        SpendingList = spendingList.content;
        PageCount = Math.ceil(spendingList.count/ItemsOnPage);
        if(PageCount === 0){
            PageCount++;
        }
        TextSpendingsCurrentPage.innerText = `Страница ${CurrentPage} из ${PageCount}`;
        setSpendingsListHTML(spendingList.content);
    });
}

function loadDebts(){
    $.getJSON(`api/spendings/all`, null, spendingList => {
        setDebtListHTML(getDebtMap(spendingList));
    });
}

function setSpendingsListHTML(spendings_list) {
    TextDebtAmount.innerText = "0";
    SpanLoadingSpendingsMessage.style.display = "none";
    let spending_amount = 0
    if (spendings_list.length > 0) {
        DivAllSpendings.innerHTML = "";
        for (let spending of spendings_list) {
            spending_amount++;
            if(CheckBoxHideZeroDebtSpendings.checked && spending.debts[Username] === 0) continue;
            let names = []
            for (let name in spending.debts) {
                names.push(name);
            }
            let names_list_content = "";
            if (names.length > 3) {
                names_list_content += names[0] + ", ";
                names_list_content += names[1];
                names_list_content += ` и ещё ${names.length - 2}`;
            } else {
                for (let i = 0; i < names.length - 1; i++) {
                    names_list_content += names[i] + ", ";
                }
                names_list_content += names[names.length - 1];
            }
            let spendingDiv = document.createElement("div");
            spendingDiv.style.margin = "5px";
            spendingDiv.style.padding = "10px";
            spendingDiv.style.borderRadius = "7px";
            spendingDiv.style.background = "#EEEEEE";

            let spendingHeader = document.createElement("h3");
            spendingHeader.appendChild(document.createTextNode("Счёт "));
            let spendingLink = document.createElement("a");
            spendingLink.href = `/spending?id=${spending.id}`
            spendingLink.innerText = spending.name;
            spendingHeader.appendChild(spendingLink);
            spendingHeader.appendChild(document.createTextNode(` от ${spending.date}`));
            spendingDiv.appendChild(spendingHeader);

            let spendingNamesList = document.createElement("p");
            spendingNamesList.innerText = names_list_content
            let spendingDebtDate = document.createElement("p");
            spendingDebtDate.innerText = spending.payerName === Username ? "Вы оплатили этот счёт" : "Ваш долг " + spending.debts[Username] + " ₽";
            spendingDiv.appendChild(spendingDebtDate)

            DivAllSpendings.appendChild(spendingDiv);
        }
    } else {
        DivAllSpendings.innerHTML = "<span style='color: gray;'>Вы не состоите ни в одном счёте</span>"
    }
    TextSpendingsAmount.innerText = spending_amount.toString();
    //setDebtListHTML(getDebtMap(spendings_list));
}

function checkCurrentDistributionMethod(){
    if(CurrentMethodValue === RadioButtonEqualParts.value){
        $(".text__equal__part").show();
        $(".input__person__explicit__amount").hide();
        $(".div__overall__amount").show();
    } else {
        $(".text__equal__part").hide();
        $(".input__person__explicit__amount").show();
        $(".div__overall__amount").hide();
    }
}

var CurrentMethodValue = null;

document.querySelectorAll(".radio__btn__distribution__method").forEach(btn => {
    btn.addEventListener("click", () => {
        CurrentMethodValue = btn.value;
        checkCurrentDistributionMethod();
    });
});

function getDebtMap(spending_list){
    let res = {}
    for(let spending of spending_list){
        if(res[spending.payerName] === undefined && spending.debts[Username] > 0){
            res[spending.payerName] = {}
        }
        if(spending.debts[Username] > 0){
            res[spending.payerName][spending.id] = {name: spending.name, amount: spending.debts[Username]};
        }
    }
    return res;
}

function setDebtListHTML(debtMap){
    let totalAmount = 0;
    DivAllDebts.innerHTML = Object.keys(debtMap).length > 0 ? "" : "<p style='color: gray'>Долгов перед пользователями нет</p>";
    let debt_count = 0;
    for(let personName of Object.keys(debtMap)){
        let personAmount = 0;
        let personDiv = document.createElement("div");
        personDiv.style.background = "#EEEEEE";
        personDiv.style.margin = "5px";
        personDiv.style.padding = "10px";
        personDiv.style.borderRadius = "7px";
        let personDivHeader = document.createElement("h3");
        let expandSpendingsList = document.createElement("button");
        expandSpendingsList.style.color = "blue";
        expandSpendingsList.innerText = "▶Счета";
        let spendingListDiv = document.createElement("div");
        spendingListDiv.style.display = "none";
        for(let spendingId of Object.keys(debtMap[personName])){
            debt_count++;
            let amount = debtMap[personName][spendingId]['amount'];
            let spendingName = debtMap[personName][spendingId]['name'];
            let spendingP = document.createElement("p");
            spendingP.appendChild(document.createTextNode("Счёт "));
            let spendingA = document.createElement("a");
            spendingA.href = `/spending?id=${spendingId}`;
            spendingA.innerText = spendingName;
            spendingP.appendChild(spendingA);
            spendingP.appendChild(document.createTextNode(`: долг ${amount}₽`));
            spendingListDiv.appendChild(spendingP)

            totalAmount += amount;
            personAmount += amount;
        }
        expandSpendingsList.addEventListener('click', () => {
            if(spendingListDiv.style.display === 'none'){
                spendingListDiv.style.display = 'block';
                expandSpendingsList.innerText = "▼Счета";
            } else {
                spendingListDiv.style.display = 'none';
                expandSpendingsList.innerText = "▶Счета";
            }
        });
        personDivHeader.innerText = `Долг перед ${personName}: ${personAmount}₽`
        personDiv.append(personDivHeader);
        personDiv.append(expandSpendingsList);
        personDiv.append(spendingListDiv);
        DivAllDebts.append(personDiv);
        SpanTotalDebtAmount.innerText = totalAmount;
        TextDebtAmount.innerText = debt_count.toString();
    }
}