var psImgsArr=[];
var allowShare=false;

function createPhotoSwipe(_obj,_pspsImgsArr,_allowShare){
psImgsArr=_pspsImgsArr;
allowShare=_allowShare;
var div=document.createElement('div');
div.setAttribute('class','pswp');
div.setAttribute('tabindex','-1');
div.setAttribute('role','dialog');
div.setAttribute('aria-hidden','true');
div.innerHTML='<div class="pswp__bg" id="div_pswp_bg"></div><div class="pswp__scroll-wrap"><div class="pswp__container"><div class="pswp__item"></div><div class="pswp__item"></div><div class="pswp__item"></div></div><div class="pswp__ui pswp__ui--hidden"><div class="pswp__top-bar"><div class="pswp__counter"></div><button class="pswp__button pswp__button--close" title="Close (Esc)"></button><button class="pswp__button pswp__button--share" title="Share"></button><button class="pswp__button pswp__button--fs" title="Toggle fullscreen"></button><button class="pswp__button pswp__button--zoom" title="Zoom in/out"></button><div class="pswp__preloader"><div class="pswp__preloader__icn"><div class="pswp__preloader__cut"><div class="pswp__preloader__donut"></div></div></div></div></div><div class="pswp__share-modal pswp__share-modal--hidden pswp__single-tap"><div class="pswp__share-tooltip"></div></div><button class="pswp__button pswp__button--arrow--left" title="Previous (arrow left)"></button><button class="pswp__button pswp__button--arrow--right" title="Next (arrow right)"></button><div class="pswp__caption"><div class="pswp__caption__center"></div></div></div></div>';
_obj.appendChild(div);
for(var i=0; i<psImgsArr.length; i++){
var aImg=new Image();
aImg.src=psImgsArr[i].src;
aImg.onload=(function(_i,_aImg){return function(){
psImgsArr[_i].w=_aImg.naturalWidth
psImgsArr[_i].h=_aImg.naturalHeight;
};})(i,aImg);
}
}

function setImgsArr(_arr){
psImgsArr=_arr;

for(var i=0; i<psImgsArr.length; i++){
var aImg=new Image();
aImg.src=psImgsArr[i].src;
aImg.onload=(function(_i,_aImg){return function(){
psImgsArr[_i].w=_aImg.naturalWidth
psImgsArr[_i].h=_aImg.naturalHeight;
};})(i,aImg);
}

}

function openPhotoSwipeCore(_inImg,_canSwipe){
var pswpElement=document.querySelectorAll('.pswp')[0];
var options={"index":_inImg,"tapToToggleControls":false,"bgOpacity":0.85,"shareEl":allowShare,"fullscreenEl":false};
var gallery=null;
if(_canSwipe)gallery=new PhotoSwipe(pswpElement,PhotoSwipeUI_Default,psImgsArr,options);
else gallery=new PhotoSwipe(pswpElement,PhotoSwipeUI_Default,[psImgsArr[_inImg]],options);
gallery.init();
//gallery.listen('beforeChange',function(){}});
}

function openPhotoSwipe(_inImg){
openPhotoSwipeCore(_inImg,true);
}

function openPhotoView(_inImg){
openPhotoSwipeCore(_inImg,false);
}