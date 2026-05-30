(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn set-visible! [^TiledMapTileLayer layer bool]
  (.setVisible layer bool))

(defn create [width height tilewidth tileheight]
  (TiledMapTileLayer. width height tilewidth tileheight))
