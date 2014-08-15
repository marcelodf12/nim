
function carga()
{
    posicion = 1;

    // IE
    if (navigator.userAgent.indexOf("MSIE") >= 1)
        navegador = 1;
    // Otros
    else
        navegador = 0;
}

function evitaEventos(event)
{
    // Funcion que evita que se ejecuten eventos adicionales
    if (navegador == 1)
    {
        window.event.cancelBubble = true;
        window.event.returnValue = false;
    }
    if (navegador == 0)
        event.preventDefault();
}

function comienzoMovimiento(event, id)
{
    elMovimiento = document.getElementById(id);

    // Obtengo la posicion del cursor
    if (navegador == 1)
    {
        cursorComienzoX = window.event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
        cursorComienzoY = window.event.clientY + document.documentElement.scrollTop + document.body.scrollTop;

        document.attachEvent("onmousemove", enMovimiento);
        document.attachEvent("onmouseup", finMovimiento);
    }
    if (navegador == 0)
    {
        cursorComienzoX = event.clientX + window.scrollX;
        cursorComienzoY = event.clientY + window.scrollY;

        document.addEventListener("mousemove", enMovimiento, true);
        document.addEventListener("mouseup", finMovimiento, true);
    }

    elComienzoX = parseInt(elMovimiento.style.left);
    elComienzoY = parseInt(elMovimiento.style.top);
    // Actualizo el posicion del elemento
    elMovimiento.style.zIndex = ++posicion;

    evitaEventos(event);
}

function enMovimiento(event)
{
    var xActual, yActual;
    if (navegador == 1)
    {
        xActual = window.event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
        yActual = window.event.clientY + document.documentElement.scrollTop + document.body.scrollTop;
    }
    if (navegador == 0)
    {
        xActual = event.clientX + window.scrollX;
        yActual = event.clientY + window.scrollY;
    }

    elMovimiento.style.left = (elComienzoX + xActual - cursorComienzoX) + "px";
    elMovimiento.style.top = (elComienzoY + yActual - cursorComienzoY) + "px";

    evitaEventos(event);
}

function finMovimiento(event)
{
    if (navegador == 1)
    {
        document.detachEvent("onmousemove", enMovimiento);
        document.detachEvent("onmouseup", finMovimiento);
    }
    if (navegador == 0)
    {
        document.removeEventListener("mousemove", enMovimiento, true);
        document.removeEventListener("mouseup", finMovimiento, true);
    }
}
window.onload=carga;