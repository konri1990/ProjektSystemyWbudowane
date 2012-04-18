/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var c=0;
var t;
var timer_is_on=0;
function start(){
    doTimer();
}

function timedCount()
{
    document.getElementById('txt').value=c;
    if(c%2==0){                
        document.getElementById('obrazek').src="images/klatka1.jpg";
    }else{                
        document.getElementById('obrazek').src="images/klatka2.jpg";
    }
    c=c+1;
    t=setTimeout("timedCount()",100);
}

function doTimer()
{
    if (!timer_is_on)
    {
        timer_is_on=1;
        timedCount();
    }
}

function stopCount()
{
    clearTimeout(t);
    timer_is_on=0;
}