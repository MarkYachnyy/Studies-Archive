Field_Container = document.querySelector(".field__container");
Solve_Button = document.querySelector(".solve__button");
N = 3;

function toArray(obj) {
  var array = [];
  for (var i = obj.length >>> 0; i--;) {
    array[i] = obj[i];
  }
  return array;
}

function createSection(size){
    let res = document.createElement("div");
    res.classList.add('section');
    for(let i = 0; i < size; i++){
        let row_div = document.createElement("div");
        for(let j = 0; j < size; j++){
            let cell = document.createElement("input");
            cell.classList.add("cell");
            est_width = Math.trunc(window.innerWidth/(2.2 * size * size));
            cell.style.width = `${est_width}px`
            cell.style.fontSize = `${Math.trunc(est_width / 2)}px`
            row_div.appendChild(cell);
        }
        res.appendChild(row_div);
    }
    return res;
}

function doAjax(value, callback) {
	let http = new XMLHttpRequest();
	let val = encodeURIComponent(value);
	http.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var text = this.responseText;
			if (typeof callback == "function") {
				callback(text);
			}
		}
	}
	http.open('GET', '/solve?data='+val, true);
	http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	http.send();
}

function setData(str){
    let cells = [].slice.call(document.querySelectorAll(".cell"));
    let num_str = str.split(' ');
    if (num_str.length != cells.length){
        return;
    }
    if (num_str.includes('0')){
        alert("Данный судоку не имеет решений");
        return;
    }
    for(let i = 0; i < cells.length; i++){
        cells[i].value = num_str[i];
    }
}

function startSolution(){
    let cells = [].slice.call(document.querySelectorAll(".cell"));
    let res = ''
    for(let i = 0; i < cells.length - 1; i++){
        let val = cells[i].value.trim();
        if (val == ''){
            res += '0';
        } else if(isNaN(Number(val)) || Number(val) < 0 || Number(val) > N * N){
            alert("Неверное значение в клетке");
            return;
        } else {
            res += val;
        }
        res += ' ';
    }
    let val = cells[cells.length - 1].value.trim();
    if (val == ''){
        res += '0';
    } else if(isNaN(Number(val)) || Number(val) < 0){
        alert("Неверное значение в клетке");
    } else {
        res += val;
    }
    Solve_Button.setAttribute("disabled", true);
    Solve_Button.classList.remove("solve__button");
    Solve_Button.classList.add("solve__button__no__hover");
    Solve_Button.innerHTML = "Подождите...";
    doAjax(res, text => {
        setData(text);
        Solve_Button.removeAttribute("disabled");
        Solve_Button.classList.remove("solve__button__no__hover");
        Solve_Button.classList.add("solve__button");
        Solve_Button.innerHTML = "РЕШИТЬ";
    });
}

function fillField(){
    Field_Container.innerHTML = '';
    for(let i = 0; i < N; i++){
    let section_row = document.createElement("div");
    section_row.style.display = 'flex';
    for(let j = 0; j < N; j++){
        let section = createSection(N);
        section_row.appendChild(section);
    }
        Field_Container.appendChild(section_row);
    }
}

for(let button of toArray(document.querySelectorAll(".size__button"))){
    button.addEventListener("click", () => {
        N = Number(button.value);
        console.log(N);
        fillField();
        document.querySelector(".solver__header__text").innerHTML = `Решить судоку ${N * N}x${N * N}`;
        Solve_Button.removeAttribute("disabled");
        Solve_Button.classList.remove("solve__button__no__hover");
        Solve_Button.classList.add("solve__button");
        Solve_Button.innerHTML = "РЕШИТЬ";
    })
}

document.querySelector(".about__sudoku__text").style.fontSize = `${Math.trunc(document.querySelector(".about__sudoku").offsetHeight/23)}px`;
document.querySelector("#button_3x3").click();
document.querySelector(".clear__button").addEventListener('click', () => {
    fillField();
})
Solve_Button.addEventListener("click", () => {
    startSolution();
})