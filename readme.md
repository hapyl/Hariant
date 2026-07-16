# Code Style

## Class Naming Convention

Every subclass must follow the parent as a name conventions, for example, all `Hero` classes
must follow the parent (`Hero`) and always start with it.

<b><u>Note that this pattern is explicitly enforced for Hero and Talent and must be strictly followed.</u></b>

#### Examples:

* `HeroArcher`
    * ✅ Perfectly named class.
* `Archer`
    * ❌ Must start with Hero
* `ArcherHero`
    * ❌ Backwards

---

## Talent Ordering

It is advised, though not required, to follow the talent order, which should be:

* **Signature Talent** - Something that defines the playstyle of the hero should be on the first slot.
* **Movement Talent** - If hero has a movement talent, it should be on the second slot, which will feel better for the
  player when using different heroes with movement talents.
* **Additional Talent** - An additional talent goes to the third slot.

## Class Packages

Every class must live in the correct package based on the ownership of the class, for example,
all `Talent`'s should be inside `Hero`'s main package, since hero **owns** talents.

## Annotations

The project must only use **JetBrains** annotations for nullability, (eg: `@Nullabe`, `@NotNull`).

## Finalization

All singleton classes (Talents, Items, etc), must be finalized (have `final` modifier on them).

------------------------------------------------------------------------

# Descriptions & Lore

## Descriptions

Description should always describe what the object does in imperative mood (like a command) and not provide
any other information other than what's necessary for the object description.

#### Examples:

* `Shoot three arrows in front of you.`
    * ✅ Perfect description.
* `Shoots three arrows in front of you.`
    * ❌ Third person, should sound like a command.
* `Shoots magical arrows that was was forged from the heart of an ancient dragon.`
    * ❌ Pointless lore integration in description.

---

## Flavor Text

Flavor text is a **short** description displayed below the description, which describe
the lore significance of an object.

#### Examples:

* `A shiny golden coin with a face of a legendary cat engraved onto it.`
    * ✅ Perfect flavor text.
* `There was once a legendary cat, who fought fiercely through every battle victorious, until one day...`
    * ❌ Too long, this is lore, not flavor text.

---

## Lore

* Lore is extended version of flavor text, that describes the story of an object. Lore can only be viewed from the
  Archive.

---

## Comments

The following should always be commented:

* Effects, be it particles, sounds or anything else. (eg: `// Fx, // Effects, // Sfx`, etc) 

---