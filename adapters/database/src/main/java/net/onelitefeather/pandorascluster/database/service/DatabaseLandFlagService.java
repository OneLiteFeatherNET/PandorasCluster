package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.flag.LandEntityCapFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandNaturalFlag;
import net.onelitefeather.pandorascluster.api.land.flag.LandRoleFlag;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandFlagService;
import net.onelitefeather.pandorascluster.api.service.result.flag.AddLandFlagResult;
import net.onelitefeather.pandorascluster.api.service.result.flag.RemoveLandFlagResult;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.flag.EntityCapFlagMapper;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMapper;
import net.onelitefeather.pandorascluster.database.mapper.flag.NaturalFlagMapper;
import net.onelitefeather.pandorascluster.database.mapper.flag.RoleFlagMapper;
import net.onelitefeather.pandorascluster.database.models.flag.LandEntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.LandRoleFlagEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public record DatabaseLandFlagService(DatabaseService databaseService) implements LandFlagService {

    @Override
    public AddLandFlagResult addRoleFlag(@NotNull LandRoleFlag roleFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        if(hasFlag(roleFlag, flagContainer)) return flagAlreadyAddedResult(roleFlag);

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            var flag = new LandRoleFlagEntity(null, roleFlag.name(), roleFlag.state(), roleFlag.role(), FlagContainerMapper.toEntity(flagContainer));

            session.persist(flag);
            transaction.commit();
            return new AddLandFlagResult.Added(roleFlag);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add land role flag.", e);
            if (transaction != null) transaction.rollback();
            return new AddLandFlagResult.Failed("Failed to add landFlag %s".formatted(roleFlag.name()));
        }
    }

    @Override
    public void updateRoleFlag(@NotNull LandRoleFlag roleFlag) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(RoleFlagMapper.toEntity(roleFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot update roleFlag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public RemoveLandFlagResult removeRoleFlag(@NotNull LandRoleFlag roleFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;

        if(flagNotFound(roleFlag, flagContainer)) return flagNotFoundResult(roleFlag);

        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.remove(RoleFlagMapper.toEntity(roleFlag));
            transaction.commit();
            return new RemoveLandFlagResult.Success(roleFlag);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove roleFlag.", e);
            if (transaction != null) transaction.rollback();
            return new RemoveLandFlagResult.Failed(roleFlag);
        }
    }

    @Override
    public AddLandFlagResult addNaturalFlag(@NotNull LandNaturalFlag naturalFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        if(hasFlag(naturalFlag, flagContainer)) return flagAlreadyAddedResult(naturalFlag);
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandNaturalFlagEntity(null, naturalFlag.name(), naturalFlag.state(), FlagContainerMapper.toEntity(flagContainer));
            session.persist(flag);
            transaction.commit();
            return new AddLandFlagResult.Added(NaturalFlagMapper.toModel(flag));
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add naturalFlag.", e);
            if (transaction != null) transaction.rollback();
            return new AddLandFlagResult.Failed("Failed to add landFlag %s".formatted(naturalFlag.name()));
        }
    }

    @Override
    public void updateNaturalFlag(@NotNull LandNaturalFlag naturalFlag) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(NaturalFlagMapper.toEntity(naturalFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot update natural flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public RemoveLandFlagResult removeNaturalFlag(@NotNull LandNaturalFlag naturalFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;

        if(flagNotFound(naturalFlag, flagContainer)) return flagNotFoundResult(naturalFlag);

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(NaturalFlagMapper.toEntity(naturalFlag));
            transaction.commit();

            return new RemoveLandFlagResult.Success(naturalFlag);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove naturalFlag.", e);
            if (transaction != null) transaction.rollback();
            return new RemoveLandFlagResult.Failed(naturalFlag);
        }
    }

    @Override
    public AddLandFlagResult addEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, FlagContainer flagContainer) {
        Transaction transaction = null;

        if(hasFlag(entityCapFlag, flagContainer)) return flagAlreadyAddedResult(entityCapFlag);
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            var flag = new LandEntityCapFlagEntity(null, entityCapFlag.name(), entityCapFlag.spawnLimit(), FlagContainerMapper.toEntity(flagContainer));
            session.persist(flag);
            transaction.commit();

            return new AddLandFlagResult.Added(EntityCapFlagMapper.toModel(flag));
        } catch (HibernateException e) {
            var result = new AddLandFlagResult.Failed("Failed to add landFlag %s".formatted(entityCapFlag.name()));
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            if (transaction != null) transaction.rollback();
            return result;
        }
    }

    @Override
    public void updateEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(EntityCapFlagMapper.toEntity(entityCapFlag));
            transaction.commit();

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot update entityCap flag.", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public RemoveLandFlagResult removeEntityCapFlag(@NotNull LandEntityCapFlag entityCapFlag, @NotNull FlagContainer flagContainer) {
        Transaction transaction = null;

        if(flagNotFound(entityCapFlag, flagContainer)) return flagNotFoundResult(entityCapFlag);

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(EntityCapFlagMapper.toEntity(entityCapFlag));
            transaction.commit();
            return new RemoveLandFlagResult.Success(entityCapFlag);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove entityCap flag.", e);
            if (transaction != null) transaction.rollback();
            return new RemoveLandFlagResult.Failed(entityCapFlag);
        }
    }

    private AddLandFlagResult.AlreadyAdded flagAlreadyAddedResult(LandFlag flag) {
        return new AddLandFlagResult.AlreadyAdded("The flag %s was already added".formatted(flag.name()));
    }

    private RemoveLandFlagResult.NotFound flagNotFoundResult(LandFlag flag) {
        return new RemoveLandFlagResult.NotFound(flag);
    }

    private boolean flagNotFound(LandFlag flag, FlagContainer container) {
        return !hasFlag(flag, container);
    }

    private boolean hasFlag(LandFlag flag, FlagContainer container) {
        return container.hasFlag(flag);
    }
}
