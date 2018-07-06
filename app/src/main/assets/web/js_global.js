var link='http://meetus.io/oopa';

var scrolled=false;

function o(_o){return document.getElementById(_o);}

function ov(_o){return document.getElementById(_o).value;}

function ovok(_o){return (ov(_o)!=null && ov(_o)!='');}

function ce(_o){return document.createElement(_o);}

function sa(_o, _k, _v){o(_o).setAttribute(_k,_v);}

function js(_j){return JSON.stringify(_j);}

function jp(_j){return JSON.parse(_j);}

function als(_j){alert(js(_j));}

function gotlwh(_o){  // Get object's top left width height
return {"t":o(_o).offsetTop, "l":o(_o).offsetLeft, "w":o(_o).offsetWidth, "h":o(_o).offsetHeight};
}

function setScrollDetect(_oarr){
for(var i=0; i<_oarr.length; i++){
o(_oarr[i]).onscroll=function(){scrolled=true;}
}
window.ontouchstart=function(){
setTimeout(function(){scrolled=false;},10);
}
}

function getMaxInArray(_array){
var indexNum=0;
for(var i=0; i<_array.length; i++){
var thisNum=_array[i];
var numAdded=0;
for(var k=0; k<_array.length; k++){
if(thisNum>=_array[k])numAdded++;
}
if(numAdded==_array.length){indexNum=i;break;}
}
return indexNum;
}

function getMinInArray(_array){
var indexNum=0;
for(var i=0; i<_array.length; i++){
var thisNum=_array[i];
var numAdded=0;
for(var k=0; k<_array.length; k++){
if(thisNum<=_array[k])numAdded++;
}
if(numAdded==_array.length){indexNum=i;break;}
}
return indexNum;
}

function sortArrayByIndex(_array){
var newArr=[];
while(_array.length>0){
var minIndex=getMinInArray(_array);
newArr.push(minIndex);
_array.splice(minIndex,1);
}
return newArr;
}

Array.prototype.sortOn=function(_key){
this.sort(function(a,b){
if(a[_key]<b[_key])return -1;
else if(a[_key]>b[_key])return 1;
return 0;
});
}

function distanceGeoPoints(_lat1, _lon1, _lat2, _lon2) {
var deg2rad=function(_deg){return _deg*(Math.PI/180);}
var R=6371; // Radius of the earth in km
var dLat=deg2rad(_lat2-_lat1);  // deg2rad below
var dLon=deg2rad(_lon2-_lon1); 
var a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(deg2rad(_lat1))*Math.cos(deg2rad(_lat2))*Math.sin(dLon/2)*Math.sin(dLon/2); 
var c=2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
var d=R*c; // Distance in km
return d;
}

function replaceAll(_str, _find, _replace) {
return _str.replace(new RegExp(_find, 'g'), _replace);
}

function addDays(_date, _days){
_date.setDate(_date.getDate()+_days);
return _date;
}

Date.prototype.yyyymmdd=function(){
var mm=this.getMonth()+1;
var dd=this.getDate();
return [this.getFullYear(),
(mm>9 ? '' : '0') + mm,
(dd>9 ? '' : '0') + dd
].join('-');
};

function getTodayDateString(){
var todayTodayString=new Date();
var todayMonthString=todayTodayString.getMonth()+1;
if(todayMonthString<10)todayMonthString='0'+todayMonthString;
var todayDateString=todayTodayString.getDate();
if(todayDateString<10)todayDateString='0'+todayDateString;
todayTodayString=todayTodayString.getFullYear()+'-'+todayMonthString+'-'+todayDateString;
return todayTodayString;
}

function getDTID(){
var todayTodayString=new Date();
var todayMonthString=todayTodayString.getMonth()+1;
if(todayMonthString<10)todayMonthString='0'+todayMonthString;
var todayDateString=todayTodayString.getDate();
if(todayDateString<10)todayDateString='0'+todayDateString;
var dtid=todayTodayString.getFullYear()+''+todayMonthString+''+todayDateString;
dtid=dtid+''+todayTodayString.getHours()+''+todayTodayString.getMinutes()+''+todayTodayString.getSeconds()+''+todayTodayString.getMilliseconds();
return dtid;
}

function getTodayDateString(){
var todayTodayString=new Date();
var todayMonthString=todayTodayString.getMonth()+1;
if(todayMonthString<10)todayMonthString='0'+todayMonthString;
var todayDateString=todayTodayString.getDate();
if(todayDateString<10)todayDateString='0'+todayDateString;
todayTodayString=todayTodayString.getFullYear()+'-'+todayMonthString+'-'+todayDateString;
return todayTodayString;
}

function getNowTime(_withMS){
var nowt=new Date();
var mi=nowt.getMinutes();
var se=nowt.getSeconds();
var ms=nowt.getMilliseconds();
if(mi<10)mi='0'+mi;
if(se<10)se='0'+se;
if(ms<10)ms='0'+ms;
if(_withMS)return nowt.getHours()+':'+mi+':'+se+'.'+ms;
return nowt.getHours()+':'+mi+':'+se;
}

function getTodayDateTime(){
return getTodayDateString()+' '+getNowTime(false);
}

function datesComparing(_startDate,_endDate){
if(_startDate.indexOf(' ')!=-1){
_startDate=_startDate.split(' ');
_startDate=_startDate[0];
}
if(_endDate.indexOf(' ')!=-1){
_endDate=_endDate.split(' ');
_endDate=_endDate[0];
}
_startDate=_startDate.split('-');
_endDate=_endDate.split('-');
return Math.ceil(new Date(_startDate[0],_startDate[1],_startDate[2]).getTime()-new Date(_endDate[0],_endDate[1],_endDate[2]).getTime())/(24*60*60*1000);
}

function getMonthEn(_m){
if(parseInt(_m)==1)return 'Jan';
else if(parseInt(_m)==2)return 'Feb';
else if(parseInt(_m)==3)return 'Mar';
else if(parseInt(_m)==4)return 'Apr';
else if(parseInt(_m)==5)return 'May';
else if(parseInt(_m)==6)return 'Jun';
else if(parseInt(_m)==7)return 'Jul';
else if(parseInt(_m)==8)return 'Aug';
else if(parseInt(_m)==9)return 'Sep';
else if(parseInt(_m)==10)return 'Oct';
else if(parseInt(_m)==11)return 'Nov';
else if(parseInt(_m)==12)return 'Dec';
return 'error';
}

function getMonthEnFull(_m){
if(parseInt(_m)==1)return 'January';
else if(parseInt(_m)==2)return 'February';
else if(parseInt(_m)==3)return 'March';
else if(parseInt(_m)==4)return 'April';
else if(parseInt(_m)==5)return 'May';
else if(parseInt(_m)==6)return 'June';
else if(parseInt(_m)==7)return 'July';
else if(parseInt(_m)==8)return 'August';
else if(parseInt(_m)==9)return 'September';
else if(parseInt(_m)==10)return 'October';
else if(parseInt(_m)==11)return 'November';
else if(parseInt(_m)==12)return 'December';
return 'error';
}

function isOldedDate(_date){
var nowD=getTodayDateString();
nowD=parseInt(replaceAll(nowD,'-',''));
_date=parseInt(replaceAll(_date,'-',''));
if(nowD>_date)return true;
return false;
}

function isInt(_value){
if(_value.toLowerCase().indexOf('a')!=-1 || _value.toLowerCase().indexOf('b')!=-1 || 
_value.toLowerCase().indexOf('c')!=-1 || _value.toLowerCase().indexOf('d')!=-1 ||
_value.toLowerCase().indexOf('e')!=-1 || _value.toLowerCase().indexOf('f')!=-1 ||
_value.toLowerCase().indexOf('g')!=-1 || _value.toLowerCase().indexOf('h')!=-1 ||
_value.toLowerCase().indexOf('i')!=-1 || _value.toLowerCase().indexOf('j')!=-1 ||
_value.toLowerCase().indexOf('k')!=-1 || _value.toLowerCase().indexOf('l')!=-1 ||
_value.toLowerCase().indexOf('m')!=-1 || _value.toLowerCase().indexOf('n')!=-1 ||
_value.toLowerCase().indexOf('o')!=-1 || _value.toLowerCase().indexOf('p')!=-1 ||
_value.toLowerCase().indexOf('q')!=-1 || _value.toLowerCase().indexOf('r')!=-1 ||
_value.toLowerCase().indexOf('s')!=-1 || _value.toLowerCase().indexOf('t')!=-1 ||
_value.toLowerCase().indexOf('u')!=-1 || _value.toLowerCase().indexOf('v')!=-1 ||
_value.toLowerCase().indexOf('w')!=-1 || _value.toLowerCase().indexOf('x')!=-1 ||
_value.toLowerCase().indexOf('y')!=-1 || _value.toLowerCase().indexOf('z')!=-1)return false;
return !isNaN(_value) && (function(x) { return (x | 0) === x; })(parseFloat(_value))
}

function createXMLHttpRequest2(_link, _dataJSON, _endFunction){
var formData=new FormData();
for(var i in _dataJSON)formData.append(i,_dataJSON[i]);
var xhr=new XMLHttpRequest();
xhr.open('POST',_link,true);
xhr.onload=function(e){
};
xhr.onreadystatechange=function(){
if((xhr.readyState==4)&&(xhr.status==200)){
try{
if(_endFunction!=null){
_endFunction(eval('('+xhr.responseText+')'));
}
}catch(e){}
}
};
xhr.send(formData);
}

function setFileOnDragListener(_obj, _cusFunction){
if(_obj!='document.body')_obj=o(_obj);
else _obj=document.body;
_obj.ondragover=function(event){
event.preventDefault();
}
_obj.ondrop=function(event){
event.preventDefault(event);
var files=event.dataTransfer.files;
if(files.length>0){
if(_cusFunction!=null)_cusFunction(files);
}
event.stopPropagation();
return false;
}
}

function previewFileToObject(_obj, _file){
var oFReader=new FileReader();
oFReader.onload=function(oFREvent){
o(_obj).src=oFREvent.target.result;
};
oFReader.readAsDataURL(_file);
}

function hasObjInArray(_arr, _obj){
for(var i=0; i<_arr.length; i++){if(_obj==_arr[i])return true;}
return false;
}

function getObjectIndexFromArray(_arr, _obj){
for(var i=0; i<_arr.length; i++){if(_obj==_arr[i])return i;}
return -1;
}

function getCookie(cname){
var name=cname+"=";
var decodedCookie=decodeURIComponent(document.cookie);
var ca=decodedCookie.split(';');
for(var i=0; i<ca.length; i++) {
var c=ca[i];
while(c.charAt(0)==' '){
c=c.substring(1);
}
if(c.indexOf(name)==0)return c.substring(name.length,c.length);
}
return "";
}