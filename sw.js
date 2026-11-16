// Fighter Khey : le jeu se lance même sans réseau, et prend toujours la dernière version quand il y a du réseau
const CACHE="fighter-khey-v3";
const FILES=["./","index.html","manifest.webmanifest","icon-192.png","icon-512.png","icon-180.png"];
self.addEventListener("install",e=>{self.skipWaiting();e.waitUntil(caches.open(CACHE).then(c=>c.addAll(FILES)).catch(()=>{}))});
self.addEventListener("activate",e=>{e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim()))});
self.addEventListener("fetch",e=>{
  if(e.request.method!=="GET"||!e.request.url.startsWith(self.location.origin))return;
  e.respondWith(fetch(e.request).then(r=>{if(r&&r.ok){const cp=r.clone();caches.open(CACHE).then(c=>c.put(e.request,cp))}return r})
    .catch(()=>caches.match(e.request).then(r=>r||caches.match("index.html"))));
});
