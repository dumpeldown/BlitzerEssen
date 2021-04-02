package de.dumpeldown.blitzer.map;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MapManager {
    private JXMapViewer mapViewer;

    public MapManager(ArrayList<PlaceEntity> allEntities) {
        mapViewer = new JXMapViewer();

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        tileFactory.setThreadPoolSize(8);

        /*
         * Startfokus der Karte auf einen Standort in der Mitte von Essen setzen
         */
        GeoPosition essen_center = new GeoPosition(51.452015, 7.012837);
        mapViewer.setZoom(6);
        mapViewer.setAddressLocation(essen_center);

        Set<DefaultWaypoint> wayPoints = new HashSet<>();
        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();

        for (PlaceEntity entity : allEntities) {
            wayPoints.add(
                    new DefaultWaypoint(
                            entity.getLatitude(),
                            entity.getLongitude()
                    )
            );
        }

        waypointPainter.setWaypoints(wayPoints);
        mapViewer.setOverlayPainter(waypointPainter);
        initListeners();
    }

    public void displayMap() {
        /*
         * Map auf JFrame anzeigen
         */
        JFrame frame = new JFrame("Blitzer in Essen");
        frame.getContentPane().add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initListeners() {
        /*
        Add scroll and pan Listeners to Window
         */
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
    }
}
