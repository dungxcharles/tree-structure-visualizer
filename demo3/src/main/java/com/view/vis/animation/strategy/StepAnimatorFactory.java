package com.view.vis.animation.strategy;

import com.model.step.StepType;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory and Registry for StepAnimationStrategies.
 * Resolves the appropriate strategy based on the StepType.
 */
public class StepAnimatorFactory {

    private final Map<StepType, StepAnimationStrategy> strategyMap;

    public StepAnimatorFactory() {
        this.strategyMap = new HashMap<>();
        initializeStrategies();
    }

    private void initializeStrategies() {
        StepAnimationStrategy searchStrategy = new SearchStepStrategy();
        StepAnimationStrategy structureStrategy = new StructureStepStrategy();
        StepAnimationStrategy balanceStrategy = new BalanceStepStrategy();
        StepAnimationStrategy rbStrategy = new RedBlackStepStrategy();

        // Register Search Strategies
        strategyMap.put(StepType.VISIT, searchStrategy);
        strategyMap.put(StepType.COMPARE, searchStrategy);
        strategyMap.put(StepType.GO_LEFT, searchStrategy);
        strategyMap.put(StepType.GO_RIGHT, searchStrategy);
        strategyMap.put(StepType.GO_CHILD, searchStrategy);
        strategyMap.put(StepType.ADD_TO_RESULT, searchStrategy);
        strategyMap.put(StepType.FOUND, searchStrategy);
        strategyMap.put(StepType.NOT_FOUND, searchStrategy);
        strategyMap.put(StepType.DONE, searchStrategy);

        // Register Structure Strategies
        strategyMap.put(StepType.INSERT_NODE, structureStrategy);
        strategyMap.put(StepType.DELETE_NODE, structureStrategy);
        strategyMap.put(StepType.REPLACE_VALUE, structureStrategy);
        strategyMap.put(StepType.ADD_CHILD, structureStrategy);
        strategyMap.put(StepType.REMOVE_CHILD, structureStrategy);
        strategyMap.put(StepType.ITERATE_CHILDREN, structureStrategy);

        // Register Balance Strategies
        strategyMap.put(StepType.UPDATE_HEIGHT, balanceStrategy);
        strategyMap.put(StepType.CHECK_BALANCE, balanceStrategy);
        strategyMap.put(StepType.ROTATE_LEFT, balanceStrategy);
        strategyMap.put(StepType.ROTATE_RIGHT, balanceStrategy);

        // Register Red-Black Strategies
        strategyMap.put(StepType.RECOLOR, rbStrategy);
        strategyMap.put(StepType.FIX_START, rbStrategy);
        strategyMap.put(StepType.TRANSPLANT, rbStrategy);
    }

    /**
     * Gets the corresponding strategy for the given StepType.
     * @param type the StepType
     * @return the StepAnimationStrategy, or null if none is registered.
     */
    public StepAnimationStrategy getStrategy(StepType type) {
        return strategyMap.get(type);
    }
}
