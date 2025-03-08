package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.flag.Flag;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import net.onelitefeather.pandorascluster.api.flag.types.NaturalFlag;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandFlagService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.flag.EntityCapFlagMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.flag.NaturalFlagMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.flag.RoleFlagMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class DatabaseLandFlagService implements LandFlagService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    public DatabaseLandFlagService(PandorasCluster pandorasCluster) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = pandorasCluster.getDatabaseService();
    }

    @Override
    public void addRoleFlag(@NotNull RoleFlag roleFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandRoleFlagEntity(null, roleFlag.getName(), roleFlag.getDefaultState(), roleFlag.getRole(), getFlagContainer(flagContainer));

            session.persist(flag);
            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add land role flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeLandRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            flagContainer.removeRoleFlag(roleFlag);

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(RoleFlagMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);

            LandRoleFlagEntity flag = (LandRoleFlagEntity) mappingContext.doMapping(roleFlag);
            session.remove(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove roleFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addNaturalFlag(@NotNull LandNaturalFlag naturalFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandNaturalFlagEntity(null, naturalFlag.getName(), naturalFlag.getState(), getFlagContainer(flagContainer));
            session.persist(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add naturalFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeLandNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(NaturalFlagMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);

            flagContainer.removeNaturalFlag(naturalFlag);
            LandNaturalFlagEntity flag = (LandNaturalFlagEntity) mappingContext.doMapping(naturalFlag);
            session.remove(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove naturalFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void addEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandEntityCapFlagEntity(null, entityCapFlag.getName(), entityCapFlag.getSpawnLimit(), getFlagContainer(flagContainer));
            session.persist(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void removeLandEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(EntityCapFlagMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);

            flagContainer.removeEntityCapFlag(entityCapFlag);
            LandEntityCapFlagEntity flag = (LandEntityCapFlagEntity) mappingContext.doMapping(entityCapFlag);
            session.remove(flag);
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public void updateLandFlag(@NotNull Flag<?> flag, @NotNull FlagContainer land) {

    }

    private FlagContainerEntity getFlagContainer(FlagContainer flagContainer) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (FlagContainerEntity) mappingContext.doMapping(flagContainer);
    }
}
