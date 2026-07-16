package me.hapyl.hariant.entity;

public record StreamRulesImpl(boolean includeSelf, boolean includeTeammates, boolean includeOthers) implements StreamRules {
}
