package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.service.*;

public interface PandorasCluster {

    LandPlayerService getLandPlayerService();

    DatabaseService getDatabaseService();

    LandService getLandService();

    LandFlagService getLandFlagService();

    StaffNotificationService getStaffNotification();

    LandAreaService getLandAreaService();
}
