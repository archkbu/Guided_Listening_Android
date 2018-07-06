function w2p_checkBrowserName(){
var w2pv_b=navigator.userAgent.toLowerCase();
if(w2pv_b.indexOf('chrome')!=-1)return 'chrome';
else if(w2pv_b.indexOf('safari')!=-1 || w2pv_b.indexOf('iphone')!=-1 || w2pv_b.indexOf('ipad')!=-1)return 'safari';
else if(w2pv_b.indexOf('firefox')!=-1)return 'firefox';
else if(w2pv_b.indexOf('msie')!=-1)return 'ie';
else if(w2pv_b.indexOf('opera')!=-1)return 'opera';
return 'none';
}
var browserName=w2p_checkBrowserName();


function Phone(){}

Phone.w2p=function(){
var argArr=[];
for(var i in arguments){argArr.push(arguments[i]);}
if(argArr.length<1)return;
var methodName=arguments[0];
argArr.splice(0,1);
if(browserName=='safari'){
var passingData={"message":methodName,"parameters":argArr};
location.href="toios://"+escape(JSON.stringify(passingData));
}else{
var argsString=JSON.stringify(argArr);
argsString=argsString.substring(1);
argsString=argsString.substring(0,argsString.length-1);
var funcCallStrart=methodName+"(";
var funcCallBody='';
for(var i=0; i<argArr.length; i++){
if(typeof argArr[i]=='string'){
if(i==0)funcCallBody=funcCallBody+"'"+argArr[i]+"'";
else funcCallBody=funcCallBody+",'"+argArr[i]+"'";
}else{
if(i==0)funcCallBody=funcCallBody+""+argArr[i];
else funcCallBody=funcCallBody+","+argArr[i];
}
}
var funcCallFull='Android.'+funcCallStrart+funcCallBody+");";
try{eval(funcCallFull);}catch(e){}
}
}



// START: setAppUsed
Phone.setAppUsed=function(_user_id){
if(_user_id!=null)Phone.w2p('setAppUsed',_user_id);
else Phone.w2p('setAppUsed');
}

Phone.setAppUsedAndToPage=function(_page, _user_id){
if(_user_id!=null)Phone.w2p('setAppUsedAndToPage',_page,_user_id);
else Phone.w2p('setAppUsedAndToPage',_page);
}
// END: setAppUsed



// START: getJsonFromServer
Phone.gjfs=[];

Phone.getJsonFromServer=function(_data, _fun){
Phone.gjfs.push(_fun);
Phone.w2p('getJsonFromServer','id='+(Phone.gjfs.length-1),JSON.stringify(_data));
}

Phone.receiveJsonFromServer=function(_id, _json){
_id=parseInt(_id.replace('id=',''));
Phone.gjfs[_id](_json);
Phone.gjfs[_id]=function(){};
}

Phone.getAndSaveJsonFromServer=function(_data, _file, _fun){
_file=_file.replace('.json','');
Phone.gjfs.push(_fun);
var j={"file":_file, "data":_data};
Phone.w2p('getAndSaveJsonFromServer','id='+(Phone.gjfs.length-1),JSON.stringify(j));
}
// END: getJsonFromServer



// START: toPage
Phone.passedByPreviousPageObject=null;

Phone.toPage=function(_page, _passToNextPageObject){
if(_passToNextPageObject!=null)Phone.w2p('toPage',_page,_passToNextPageObject);
else Phone.w2p('toPage',_page);
}

Phone.toPageWithLink=function(_page, _passToNextPageObject){
if(_passToNextPageObject.hidesBackButton==null)_passToNextPageObject.hidesBackButton=false;
if(_passToNextPageObject.hidesNavigationBar==null)_passToNextPageObject.hidesNavigationBar=false;
if(_passToNextPageObject.title==null){
if(document.title!=null && document.title!='')_passToNextPageObject.title=document.title;
else _passToNextPageObject.title='';
}
if(browserName!='safari')_passToNextPageObject=JSON.stringify(_passToNextPageObject);
Phone.w2p('toPageWithLink',_page,_passToNextPageObject);
}

Phone.setPassedByPreviousPageObject=function(_json){
Phone.passedByPreviousPageObject=_json;
}

Phone.getPassedByPreviousPageObject=function(){
return Phone.passedByPreviousPageObject;
}

Phone.back=function(){
Phone.w2p('back');
}

Phone.close=function(){
Phone.w2p('close');
}
// END: toPage



// START: alert
Phone.alert=function(_title, _msg, _cancelButton){
if(_cancelButton==null)_cancelButton='OK';
Phone.w2p('alert', JSON.stringify({"title":_title, "msg":_msg, "cancel":_cancelButton}));
}

Phone.alertConfirmFun=null;
Phone.confirmAlert=function(_title, _msg, _confirmButton, _confirmFun, _cancelButton){
if(_cancelButton==null)_cancelButton='OK';
Phone.alertConfirmFun=_confirmFun;
Phone.w2p('confirmAlert', JSON.stringify({"title":_title, "msg":_msg, "confirm":_confirmButton, "cancel":_cancelButton}));
}

Phone.alertConfirmed=function(){
Phone.alertConfirmFun();
}

Phone.toast=function(_msg){
Phone.w2p('toast',_msg);
}

Phone.answeredFun=null;
Phone.prompt=function(_title, _msg, _placeholder, _answerButton, _answeredFun, _cancelButton){
Phone.answeredFun=_answeredFun;
Phone.w2p('prompt', JSON.stringify({"title":_title, "msg":_msg, "placeholder":_placeholder, "confirm":_answerButton, "cancel":_cancelButton}));
}

Phone.promptAnswered=function(_msg){
Phone.answeredFun(_msg);
}
// END: alert



// START: getJSONFromAPP
Phone.receiveJSONFromAPPFun=null;
Phone.receiveJSONsFromAPPFun=null;

Phone.getJsonFromAPP=function(_file, _isFromDocuments, _fun){
_file=_file.replace('.json','');
Phone.receiveJSONFromAPPFun=_fun;
if(browserName=='safari'){
if(_isFromDocuments==true)_isFromDocuments='YES';
else _isFromDocuments='NO';
}
Phone.w2p('getJsonFromAPP',_file,_isFromDocuments);
}

Phone.getJsonsFromAPP=function(_files, _isFromDocuments, _fun){
for(var i=0; i<_files.length; i++){
_files[i]=_files[i].replace('.json','');
}
Phone.receiveJSONsFromAPPFun=_fun;
if(browserName=='safari'){
if(_isFromDocuments==true)_isFromDocuments='YES';
else _isFromDocuments='NO';
}
Phone.w2p('getJsonsFromAPP',JSON.stringify(_files),_isFromDocuments);
}

Phone.receiveJsonFromAPP=function(_json){
if(Phone.receiveJSONFromAPPFun!=null)Phone.receiveJSONFromAPPFun(_json);
}

Phone.receiveJsonsFromAPP=function(_json){
if(Phone.receiveJSONsFromAPPFun!=null)Phone.receiveJSONsFromAPPFun(_json);
}

Phone.saveJsonToAPP=function(_file, _json){
Phone.w2p('saveJsonToAPP',_file,JSON.stringify(_json));
}
// END: getJSONFromAPP



// START: open browser
Phone.openBrowser=function(_link){
var ht='http';
if(_link.indexOf('http://')!=-1)_link=_link.replace('http://','');
else if(_link.indexOf('https://')!=-1){ht='https';_link=_link.replace('https://','');}
Phone.w2p('openBrowser',ht,_link);
}
// END: open browser



// START: GPS
Phone.gpsFun=null;

Phone.getGPS=function(_fun){
Phone.gpsFun=_fun;
Phone.w2p('getGPS');
}

Phone.setGPS=function(_latitude, _longitude){
if(Phone.gpsFun!=null)Phone.gpsFun(_latitude, _longitude);
}
// END: GPS



// START: Log
Phone.log=function(_str){
Phone.w2p('log',_str);
}
// END: Log



// START: Bookmarks
Phone.receiveBMFun=null;
Phone.receiveBookmarksFun=null;
Phone.receiveHasBookmarksFun=null;

Phone.useBookmarks=function(_name){
Phone.w2p('useBookmarks',_name);
}

Phone.getBookmarks=function(_name, _fun){
Phone.receiveBookmarksFun=_fun;
Phone.w2p('getBookmarks',_name);
}

Phone.receiveBookmarks=function(_result){
if(Phone.receiveBookmarksFun!=null)Phone.receiveBookmarksFun(_result);
}

Phone.checkHasBookmarks=function(_names, _fun){
Phone.receiveHasBookmarksFun=_fun;
Phone.w2p('checkHasBookmarks',JSON.stringify(_names));
}

Phone.receiveHasBookmarks=function(_result){
if(Phone.receiveHasBookmarksFun!=null)Phone.receiveHasBookmarksFun(_result);
}

Phone.hasBookmarked=function(_name, _stringId, _fun){
Phone.receiveBMFun=_fun;
Phone.w2p('hasBookmarked',_name+'#####PHONEW2PBM#####'+_stringId);
}

Phone.receiveHasBookmarked=function(_result){
if(Phone.receiveBMFun!=null)Phone.receiveBMFun(_result);
}

Phone.addBookmark=function(_name, _stringId){
Phone.w2p('addBookmark', _name, _stringId);
}

Phone.removeBookmark=function(_name, _stringId){
Phone.w2p('removeBookmark', _name, _stringId);
}
// END: Bookmarks