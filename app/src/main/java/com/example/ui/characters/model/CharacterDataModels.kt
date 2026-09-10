package com.example.ui.characters.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CharacterGalleryItem(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val category: String = "Other Visual Reference",
    val caption: String = "",
    val isPrimary: Boolean = false
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("uri", uri)
        put("category", category)
        put("caption", caption)
        put("isPrimary", isPrimary)
    }

    companion object {
        val CATEGORIES = listOf(
            "Face Reference",
            "Full-body Reference",
            "Clothing & Attire",
            "Hairstyle",
            "Weapons & Gear",
            "Accessories & Jewelry",
            "Different Ages",
            "Expressions",
            "Other Visual Reference"
        )

        fun fromJson(json: JSONObject): CharacterGalleryItem = CharacterGalleryItem(
            id = json.optString("id", UUID.randomUUID().toString()),
            uri = json.optString("uri", ""),
            category = json.optString("category", "Other Visual Reference"),
            caption = json.optString("caption", ""),
            isPrimary = json.optBoolean("isPrimary", false)
        )

        fun parseList(jsonString: String): List<CharacterGalleryItem> {
            val list = mutableListOf<CharacterGalleryItem>()
            if (jsonString.isBlank()) return list
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj != null) list.add(fromJson(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun serializeList(list: List<CharacterGalleryItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

data class CharacterAppearanceData(
    val height: String = "",
    val weight: String = "",
    val build: String = "",
    val bodyType: String = "",
    val skinTone: String = "",
    val hairColor: String = "",
    val hairStyle: String = "",
    val eyeColor: String = "",
    val facialFeatures: String = "",
    val faceShape: String = "",
    val distinguishingFeatures: String = "",
    val scars: String = "",
    val tattoos: String = "",
    val birthmarks: String = "",
    val piercings: String = "",
    val clothingStyle: String = "",
    val accessories: String = "",
    val typicalExpression: String = "",
    val voice: String = "",
    val accent: String = "",
    val bodyLanguage: String = "",
    val mannerisms: String = "",
    val detailedNotes: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("height", height)
        put("weight", weight)
        put("build", build)
        put("bodyType", bodyType)
        put("skinTone", skinTone)
        put("hairColor", hairColor)
        put("hairStyle", hairStyle)
        put("eyeColor", eyeColor)
        put("facialFeatures", facialFeatures)
        put("faceShape", faceShape)
        put("distinguishingFeatures", distinguishingFeatures)
        put("scars", scars)
        put("tattoos", tattoos)
        put("birthmarks", birthmarks)
        put("piercings", piercings)
        put("clothingStyle", clothingStyle)
        put("accessories", accessories)
        put("typicalExpression", typicalExpression)
        put("voice", voice)
        put("accent", accent)
        put("bodyLanguage", bodyLanguage)
        put("mannerisms", mannerisms)
        put("detailedNotes", detailedNotes)
    }

    companion object {
        fun fromJsonString(str: String): CharacterAppearanceData {
            if (str.isBlank()) return CharacterAppearanceData()
            return try {
                val json = JSONObject(str)
                CharacterAppearanceData(
                    height = json.optString("height", ""),
                    weight = json.optString("weight", ""),
                    build = json.optString("build", ""),
                    bodyType = json.optString("bodyType", ""),
                    skinTone = json.optString("skinTone", ""),
                    hairColor = json.optString("hairColor", ""),
                    hairStyle = json.optString("hairStyle", ""),
                    eyeColor = json.optString("eyeColor", ""),
                    facialFeatures = json.optString("facialFeatures", ""),
                    faceShape = json.optString("faceShape", ""),
                    distinguishingFeatures = json.optString("distinguishingFeatures", ""),
                    scars = json.optString("scars", ""),
                    tattoos = json.optString("tattoos", ""),
                    birthmarks = json.optString("birthmarks", ""),
                    piercings = json.optString("piercings", ""),
                    clothingStyle = json.optString("clothingStyle", ""),
                    accessories = json.optString("accessories", ""),
                    typicalExpression = json.optString("typicalExpression", ""),
                    voice = json.optString("voice", ""),
                    accent = json.optString("accent", ""),
                    bodyLanguage = json.optString("bodyLanguage", ""),
                    mannerisms = json.optString("mannerisms", ""),
                    detailedNotes = json.optString("detailedNotes", "")
                )
            } catch (e: Exception) {
                CharacterAppearanceData()
            }
        }
    }
}

data class CharacterPersonalityData(
    val temperament: String = "",
    val positiveTraits: String = "",
    val negativeTraits: String = "",
    val strengths: String = "",
    val weaknesses: String = "",
    val habits: String = "",
    val mannerisms: String = "",
    val likes: String = "",
    val dislikes: String = "",
    val hobbies: String = "",
    val interests: String = "",
    val petPeeves: String = "",
    val fears: String = "",
    val insecurities: String = "",
    val values: String = "",
    val beliefs: String = "",
    val morals: String = "",
    val personalBoundaries: String = "",
    val senseOfHumor: String = "",
    val socialBehavior: String = "",
    val emotionalBehavior: String = "",
    val communicationStyle: String = "",
    val notes: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("temperament", temperament)
        put("positiveTraits", positiveTraits)
        put("negativeTraits", negativeTraits)
        put("strengths", strengths)
        put("weaknesses", weaknesses)
        put("habits", habits)
        put("mannerisms", mannerisms)
        put("likes", likes)
        put("dislikes", dislikes)
        put("hobbies", hobbies)
        put("interests", interests)
        put("petPeeves", petPeeves)
        put("fears", fears)
        put("insecurities", insecurities)
        put("values", values)
        put("beliefs", beliefs)
        put("morals", morals)
        put("personalBoundaries", personalBoundaries)
        put("senseOfHumor", senseOfHumor)
        put("socialBehavior", socialBehavior)
        put("emotionalBehavior", emotionalBehavior)
        put("communicationStyle", communicationStyle)
        put("notes", notes)
    }

    companion object {
        fun fromJsonString(str: String): CharacterPersonalityData {
            if (str.isBlank()) return CharacterPersonalityData()
            return try {
                val json = JSONObject(str)
                CharacterPersonalityData(
                    temperament = json.optString("temperament", ""),
                    positiveTraits = json.optString("positiveTraits", ""),
                    negativeTraits = json.optString("negativeTraits", ""),
                    strengths = json.optString("strengths", ""),
                    weaknesses = json.optString("weaknesses", ""),
                    habits = json.optString("habits", ""),
                    mannerisms = json.optString("mannerisms", ""),
                    likes = json.optString("likes", ""),
                    dislikes = json.optString("dislikes", ""),
                    hobbies = json.optString("hobbies", ""),
                    interests = json.optString("interests", ""),
                    petPeeves = json.optString("petPeeves", ""),
                    fears = json.optString("fears", ""),
                    insecurities = json.optString("insecurities", ""),
                    values = json.optString("values", ""),
                    beliefs = json.optString("beliefs", ""),
                    morals = json.optString("morals", ""),
                    personalBoundaries = json.optString("personalBoundaries", ""),
                    senseOfHumor = json.optString("senseOfHumor", ""),
                    socialBehavior = json.optString("socialBehavior", ""),
                    emotionalBehavior = json.optString("emotionalBehavior", ""),
                    communicationStyle = json.optString("communicationStyle", ""),
                    notes = json.optString("notes", "")
                )
            } catch (e: Exception) {
                CharacterPersonalityData()
            }
        }
    }
}

data class CharacterPsychologyData(
    val coreDesire: String = "",
    val primaryMotivation: String = "",
    val secondaryMotivations: String = "",
    val greatestFear: String = "",
    val greatestRegret: String = "",
    val biggestInsecurity: String = "",
    val internalConflict: String = "",
    val externalConflict: String = "",
    val emotionalWounds: String = "",
    val secrets: String = "",
    val guilt: String = "",
    val shame: String = "",
    val beliefs: String = "",
    val misconceptions: String = "",
    val personalPhilosophy: String = "",
    val moralBoundaries: String = "",
    val breakingPoint: String = "",
    val makesHappy: String = "",
    val makesAngry: String = "",
    val makesSad: String = "",
    val makesAfraid: String = "",
    val refuseToAdmit: String = "",
    val wantOthersToThink: String = "",
    val actuallyThinkOfSelf: String = "",
    val notes: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("coreDesire", coreDesire)
        put("primaryMotivation", primaryMotivation)
        put("secondaryMotivations", secondaryMotivations)
        put("greatestFear", greatestFear)
        put("greatestRegret", greatestRegret)
        put("biggestInsecurity", biggestInsecurity)
        put("internalConflict", internalConflict)
        put("externalConflict", externalConflict)
        put("emotionalWounds", emotionalWounds)
        put("secrets", secrets)
        put("guilt", guilt)
        put("shame", shame)
        put("beliefs", beliefs)
        put("misconceptions", misconceptions)
        put("personalPhilosophy", personalPhilosophy)
        put("moralBoundaries", moralBoundaries)
        put("breakingPoint", breakingPoint)
        put("makesHappy", makesHappy)
        put("makesAngry", makesAngry)
        put("makesSad", makesSad)
        put("makesAfraid", makesAfraid)
        put("refuseToAdmit", refuseToAdmit)
        put("wantOthersToThink", wantOthersToThink)
        put("actuallyThinkOfSelf", actuallyThinkOfSelf)
        put("notes", notes)
    }

    companion object {
        fun fromJsonString(str: String): CharacterPsychologyData {
            if (str.isBlank()) return CharacterPsychologyData()
            return try {
                val json = JSONObject(str)
                CharacterPsychologyData(
                    coreDesire = json.optString("coreDesire", ""),
                    primaryMotivation = json.optString("primaryMotivation", ""),
                    secondaryMotivations = json.optString("secondaryMotivations", ""),
                    greatestFear = json.optString("greatestFear", ""),
                    greatestRegret = json.optString("greatestRegret", ""),
                    biggestInsecurity = json.optString("biggestInsecurity", ""),
                    internalConflict = json.optString("internalConflict", ""),
                    externalConflict = json.optString("externalConflict", ""),
                    emotionalWounds = json.optString("emotionalWounds", ""),
                    secrets = json.optString("secrets", ""),
                    guilt = json.optString("guilt", ""),
                    shame = json.optString("shame", ""),
                    beliefs = json.optString("beliefs", ""),
                    misconceptions = json.optString("misconceptions", ""),
                    personalPhilosophy = json.optString("personalPhilosophy", ""),
                    moralBoundaries = json.optString("moralBoundaries", ""),
                    breakingPoint = json.optString("breakingPoint", ""),
                    makesHappy = json.optString("makesHappy", ""),
                    makesAngry = json.optString("makesAngry", ""),
                    makesSad = json.optString("makesSad", ""),
                    makesAfraid = json.optString("makesAfraid", ""),
                    refuseToAdmit = json.optString("refuseToAdmit", ""),
                    wantOthersToThink = json.optString("wantOthersToThink", ""),
                    actuallyThinkOfSelf = json.optString("actuallyThinkOfSelf", ""),
                    notes = json.optString("notes", "")
                )
            } catch (e: Exception) {
                CharacterPsychologyData()
            }
        }
    }
}

data class CharacterTimelineEvent(
    val id: String = UUID.randomUUID().toString(),
    val ageOrYear: String = "",
    val title: String = "",
    val description: String = "",
    val significance: String = "Major" // Minor, Moderate, Major, Critical Turning Point
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("ageOrYear", ageOrYear)
        put("title", title)
        put("description", description)
        put("significance", significance)
    }

    companion object {
        fun fromJson(json: JSONObject): CharacterTimelineEvent = CharacterTimelineEvent(
            id = json.optString("id", UUID.randomUUID().toString()),
            ageOrYear = json.optString("ageOrYear", ""),
            title = json.optString("title", ""),
            description = json.optString("description", ""),
            significance = json.optString("significance", "Major")
        )
    }
}

data class CharacterHistoryData(
    val childhood: String = "",
    val parents: String = "",
    val family: String = "",
    val siblings: String = "",
    val childhoodEnvironment: String = "",
    val education: String = "",
    val importantFriendships: String = "",
    val importantEnemies: String = "",
    val firstLove: String = "",
    val majorAchievements: String = "",
    val majorFailures: String = "",
    val traumaticEvents: String = "",
    val importantMemories: String = "",
    val turningPoints: String = "",
    val previousRelationships: String = "",
    val previousOccupations: String = "",
    val majorLifeEvents: String = "",
    val timeline: List<CharacterTimelineEvent> = emptyList()
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("childhood", childhood)
        put("parents", parents)
        put("family", family)
        put("siblings", siblings)
        put("childhoodEnvironment", childhoodEnvironment)
        put("education", education)
        put("importantFriendships", importantFriendships)
        put("importantEnemies", importantEnemies)
        put("firstLove", firstLove)
        put("majorAchievements", majorAchievements)
        put("majorFailures", majorFailures)
        put("traumaticEvents", traumaticEvents)
        put("importantMemories", importantMemories)
        put("turningPoints", turningPoints)
        put("previousRelationships", previousRelationships)
        put("previousOccupations", previousOccupations)
        put("majorLifeEvents", majorLifeEvents)
        val arr = JSONArray()
        timeline.forEach { arr.put(it.toJson()) }
        put("timeline", arr)
    }

    companion object {
        fun fromJsonString(str: String): CharacterHistoryData {
            if (str.isBlank()) return CharacterHistoryData()
            return try {
                val json = JSONObject(str)
                val timelineList = mutableListOf<CharacterTimelineEvent>()
                val timelineArr = json.optJSONArray("timeline")
                if (timelineArr != null) {
                    for (i in 0 until timelineArr.length()) {
                        val itemObj = timelineArr.optJSONObject(i)
                        if (itemObj != null) timelineList.add(CharacterTimelineEvent.fromJson(itemObj))
                    }
                }
                CharacterHistoryData(
                    childhood = json.optString("childhood", ""),
                    parents = json.optString("parents", ""),
                    family = json.optString("family", ""),
                    siblings = json.optString("siblings", ""),
                    childhoodEnvironment = json.optString("childhoodEnvironment", ""),
                    education = json.optString("education", ""),
                    importantFriendships = json.optString("importantFriendships", ""),
                    importantEnemies = json.optString("importantEnemies", ""),
                    firstLove = json.optString("firstLove", ""),
                    majorAchievements = json.optString("majorAchievements", ""),
                    majorFailures = json.optString("majorFailures", ""),
                    traumaticEvents = json.optString("traumaticEvents", ""),
                    importantMemories = json.optString("importantMemories", ""),
                    turningPoints = json.optString("turningPoints", ""),
                    previousRelationships = json.optString("previousRelationships", ""),
                    previousOccupations = json.optString("previousOccupations", ""),
                    majorLifeEvents = json.optString("majorLifeEvents", ""),
                    timeline = timelineList
                )
            } catch (e: Exception) {
                CharacterHistoryData()
            }
        }
    }
}

data class CharacterGoalItem(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val goalType: String = "Short-term", // Short-term, Long-term, External, Internal, Secret, Completed, Failed
    val motivation: String = "",
    val obstacle: String = "",
    val importance: String = "Medium", // Low, Medium, High, Crucial
    val status: String = "Active", // Active, Completed, Failed, Abandoned
    val relatedScenes: String = "",
    val relatedCharacters: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("description", description)
        put("goalType", goalType)
        put("motivation", motivation)
        put("obstacle", obstacle)
        put("importance", importance)
        put("status", status)
        put("relatedScenes", relatedScenes)
        put("relatedCharacters", relatedCharacters)
    }

    companion object {
        val GOAL_TYPES = listOf("Short-term", "Long-term", "External", "Internal", "Secret", "Completed", "Failed")
        val IMPORTANCE_LEVELS = listOf("Low", "Medium", "High", "Crucial")
        val STATUS_OPTIONS = listOf("Active", "Completed", "Failed", "Abandoned")

        fun fromJson(json: JSONObject): CharacterGoalItem = CharacterGoalItem(
            id = json.optString("id", UUID.randomUUID().toString()),
            description = json.optString("description", ""),
            goalType = json.optString("goalType", "Short-term"),
            motivation = json.optString("motivation", ""),
            obstacle = json.optString("obstacle", ""),
            importance = json.optString("importance", "Medium"),
            status = json.optString("status", "Active"),
            relatedScenes = json.optString("relatedScenes", ""),
            relatedCharacters = json.optString("relatedCharacters", "")
        )

        fun parseList(jsonString: String): List<CharacterGoalItem> {
            val list = mutableListOf<CharacterGoalItem>()
            if (jsonString.isBlank()) return list
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj != null) list.add(fromJson(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun serializeList(list: List<CharacterGoalItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

data class CharacterArcData(
    val startingPersonality: String = "",
    val startingBeliefs: String = "",
    val startingWorldview: String = "",
    val initialFlaws: String = "",
    val initialGoals: String = "",
    val majorTurningPoints: String = "",
    val characterChallenges: String = "",
    val lessonsLearned: String = "",
    val majorChanges: String = "",
    val relationshipChanges: String = "",
    val finalBeliefs: String = "",
    val finalPersonality: String = "",
    val finalGoals: String = "",
    val endState: String = "",
    val linkedScenes: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("startingPersonality", startingPersonality)
        put("startingBeliefs", startingBeliefs)
        put("startingWorldview", startingWorldview)
        put("initialFlaws", initialFlaws)
        put("initialGoals", initialGoals)
        put("majorTurningPoints", majorTurningPoints)
        put("characterChallenges", characterChallenges)
        put("lessonsLearned", lessonsLearned)
        put("majorChanges", majorChanges)
        put("relationshipChanges", relationshipChanges)
        put("finalBeliefs", finalBeliefs)
        put("finalPersonality", finalPersonality)
        put("finalGoals", finalGoals)
        put("endState", endState)
        put("linkedScenes", linkedScenes)
    }

    companion object {
        fun fromJsonString(str: String): CharacterArcData {
            if (str.isBlank()) return CharacterArcData()
            return try {
                val json = JSONObject(str)
                CharacterArcData(
                    startingPersonality = json.optString("startingPersonality", ""),
                    startingBeliefs = json.optString("startingBeliefs", ""),
                    startingWorldview = json.optString("startingWorldview", ""),
                    initialFlaws = json.optString("initialFlaws", ""),
                    initialGoals = json.optString("initialGoals", ""),
                    majorTurningPoints = json.optString("majorTurningPoints", ""),
                    characterChallenges = json.optString("characterChallenges", ""),
                    lessonsLearned = json.optString("lessonsLearned", ""),
                    majorChanges = json.optString("majorChanges", ""),
                    relationshipChanges = json.optString("relationshipChanges", ""),
                    finalBeliefs = json.optString("finalBeliefs", ""),
                    finalPersonality = json.optString("finalPersonality", ""),
                    finalGoals = json.optString("finalGoals", ""),
                    endState = json.optString("endState", ""),
                    linkedScenes = json.optString("linkedScenes", "")
                )
            } catch (e: Exception) {
                CharacterArcData()
            }
        }
    }
}

data class CharacterVoiceData(
    val speakingStyle: String = "",
    val vocabulary: String = "",
    val sentenceLength: String = "",
    val formality: String = "",
    val slang: String = "",
    val accent: String = "",
    val favoriteExpressions: String = "",
    val wordsFrequentlyUsed: String = "",
    val wordsNeverUsed: String = "",
    val speechPatterns: String = "",
    val emotionalSpeakingPatterns: String = "",
    val notes: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("speakingStyle", speakingStyle)
        put("vocabulary", vocabulary)
        put("sentenceLength", sentenceLength)
        put("formality", formality)
        put("slang", slang)
        put("accent", accent)
        put("favoriteExpressions", favoriteExpressions)
        put("wordsFrequentlyUsed", wordsFrequentlyUsed)
        put("wordsNeverUsed", wordsNeverUsed)
        put("speechPatterns", speechPatterns)
        put("emotionalSpeakingPatterns", emotionalSpeakingPatterns)
        put("notes", notes)
    }

    companion object {
        fun fromJsonString(str: String): CharacterVoiceData {
            if (str.isBlank()) return CharacterVoiceData()
            return try {
                val json = JSONObject(str)
                CharacterVoiceData(
                    speakingStyle = json.optString("speakingStyle", ""),
                    vocabulary = json.optString("vocabulary", ""),
                    sentenceLength = json.optString("sentenceLength", ""),
                    formality = json.optString("formality", ""),
                    slang = json.optString("slang", ""),
                    accent = json.optString("accent", ""),
                    favoriteExpressions = json.optString("favoriteExpressions", ""),
                    wordsFrequentlyUsed = json.optString("wordsFrequentlyUsed", ""),
                    wordsNeverUsed = json.optString("wordsNeverUsed", ""),
                    speechPatterns = json.optString("speechPatterns", ""),
                    emotionalSpeakingPatterns = json.optString("emotionalSpeakingPatterns", ""),
                    notes = json.optString("notes", "")
                )
            } catch (e: Exception) {
                CharacterVoiceData()
            }
        }
    }
}

data class CharacterKnowledgeData(
    val factsKnown: String = "",
    val secretsKnown: String = "",
    val secretsNotKnown: String = "",
    val misconceptions: String = "",
    val suspicions: String = "",
    val importantInfo: String = "",
    val peopleKnown: String = "",
    val placesKnown: String = "",
    val eventsWitnessed: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("factsKnown", factsKnown)
        put("secretsKnown", secretsKnown)
        put("secretsNotKnown", secretsNotKnown)
        put("misconceptions", misconceptions)
        put("suspicions", suspicions)
        put("importantInfo", importantInfo)
        put("peopleKnown", peopleKnown)
        put("placesKnown", placesKnown)
        put("eventsWitnessed", eventsWitnessed)
    }

    companion object {
        fun fromJsonString(str: String): CharacterKnowledgeData {
            if (str.isBlank()) return CharacterKnowledgeData()
            return try {
                val json = JSONObject(str)
                CharacterKnowledgeData(
                    factsKnown = json.optString("factsKnown", ""),
                    secretsKnown = json.optString("secretsKnown", ""),
                    secretsNotKnown = json.optString("secretsNotKnown", ""),
                    misconceptions = json.optString("misconceptions", ""),
                    suspicions = json.optString("suspicions", ""),
                    importantInfo = json.optString("importantInfo", ""),
                    peopleKnown = json.optString("peopleKnown", ""),
                    placesKnown = json.optString("placesKnown", ""),
                    eventsWitnessed = json.optString("eventsWitnessed", "")
                )
            } catch (e: Exception) {
                CharacterKnowledgeData()
            }
        }
    }
}

data class CharacterInventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "Personal Possession", // Weapon, Clothing, Jewelry, Important Object, Personal Possession, Vehicle, Book, Technology, Prop
    val description: String = "",
    val significance: String = "",
    val linkedPropId: String? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("category", category)
        put("description", description)
        put("significance", significance)
        put("linkedPropId", linkedPropId ?: JSONObject.NULL)
    }

    companion object {
        val CATEGORIES = listOf(
            "Weapon",
            "Clothing & Attire",
            "Jewelry & Adornment",
            "Important Object",
            "Personal Possession",
            "Vehicle & Mount",
            "Book & Document",
            "Technology & Tool",
            "Story Prop"
        )

        fun fromJson(json: JSONObject): CharacterInventoryItem = CharacterInventoryItem(
            id = json.optString("id", UUID.randomUUID().toString()),
            name = json.optString("name", ""),
            category = json.optString("category", "Personal Possession"),
            description = json.optString("description", ""),
            significance = json.optString("significance", ""),
            linkedPropId = if (json.isNull("linkedPropId")) null else json.optString("linkedPropId")
        )

        fun parseList(jsonString: String): List<CharacterInventoryItem> {
            val list = mutableListOf<CharacterInventoryItem>()
            if (jsonString.isBlank()) return list
            try {
                val array = JSONArray(jsonString)
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj != null) list.add(fromJson(obj))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun serializeList(list: List<CharacterInventoryItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

data class CharacterLocationsData(
    val currentLocation: String = "",
    val previousLocations: String = "",
    val importantLocations: String = "",
    val associatedScenes: String = "",
    val linkedLocationIds: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("currentLocation", currentLocation)
        put("previousLocations", previousLocations)
        put("importantLocations", importantLocations)
        put("associatedScenes", associatedScenes)
        put("linkedLocationIds", linkedLocationIds)
    }

    companion object {
        fun fromJsonString(str: String): CharacterLocationsData {
            if (str.isBlank()) return CharacterLocationsData()
            return try {
                val json = JSONObject(str)
                CharacterLocationsData(
                    currentLocation = json.optString("currentLocation", ""),
                    previousLocations = json.optString("previousLocations", ""),
                    importantLocations = json.optString("importantLocations", ""),
                    associatedScenes = json.optString("associatedScenes", ""),
                    linkedLocationIds = json.optString("linkedLocationIds", "")
                )
            } catch (e: Exception) {
                CharacterLocationsData()
            }
        }
    }
}

data class CharacterCustomField(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val value: String
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("label", label)
        put("value", value)
    }

    companion object {
        fun fromJson(json: JSONObject): CharacterCustomField = CharacterCustomField(
            id = json.optString("id", UUID.randomUUID().toString()),
            label = json.optString("label", ""),
            value = json.optString("value", "")
        )

        fun parseList(jsonString: String): List<CharacterCustomField> {
            val list = mutableListOf<CharacterCustomField>()
            if (jsonString.isBlank()) return list
            try {
                // If it's a JSON array of objects
                if (jsonString.trim().startsWith("[")) {
                    val array = JSONArray(jsonString)
                    for (i in 0 until array.length()) {
                        val obj = array.optJSONObject(i)
                        if (obj != null) list.add(fromJson(obj))
                    }
                } else if (jsonString.trim().startsWith("{")) {
                    // Fallback if legacy map was used
                    val map = JSONObject(jsonString)
                    val keys = map.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        list.add(CharacterCustomField(label = k, value = map.optString(k, "")))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        fun serializeList(list: List<CharacterCustomField>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

typealias CharacterReferenceImage = CharacterGalleryItem

object CharacterDataSerializers {
    fun deserializeGallery(json: String): List<CharacterGalleryItem> = CharacterGalleryItem.parseList(json)
    fun serializeGallery(list: List<CharacterGalleryItem>): String = CharacterGalleryItem.serializeList(list)

    fun deserializeAppearance(json: String): CharacterAppearanceData = CharacterAppearanceData.fromJsonString(json)
    fun serializeAppearance(data: CharacterAppearanceData): String = data.toJson().toString()

    fun deserializePersonality(json: String): CharacterPersonalityData = CharacterPersonalityData.fromJsonString(json)
    fun serializePersonality(data: CharacterPersonalityData): String = data.toJson().toString()

    fun deserializePsychology(json: String): CharacterPsychologyData = CharacterPsychologyData.fromJsonString(json)
    fun serializePsychology(data: CharacterPsychologyData): String = data.toJson().toString()

    fun deserializeHistory(json: String): CharacterHistoryData = CharacterHistoryData.fromJsonString(json)
    fun serializeHistory(data: CharacterHistoryData): String = data.toJson().toString()

    fun deserializeGoals(json: String): List<CharacterGoalItem> = CharacterGoalItem.parseList(json)
    fun serializeGoals(list: List<CharacterGoalItem>): String = CharacterGoalItem.serializeList(list)

    fun deserializeArc(json: String): CharacterArcData = CharacterArcData.fromJsonString(json)
    fun serializeArc(data: CharacterArcData): String = data.toJson().toString()

    fun deserializeVoice(json: String): CharacterVoiceData = CharacterVoiceData.fromJsonString(json)
    fun serializeVoice(data: CharacterVoiceData): String = data.toJson().toString()

    fun deserializeKnowledge(json: String): CharacterKnowledgeData = CharacterKnowledgeData.fromJsonString(json)
    fun serializeKnowledge(data: CharacterKnowledgeData): String = data.toJson().toString()

    fun deserializeInventory(json: String): List<CharacterInventoryItem> = CharacterInventoryItem.parseList(json)
    fun serializeInventory(list: List<CharacterInventoryItem>): String = CharacterInventoryItem.serializeList(list)

    fun deserializeLocations(json: String): CharacterLocationsData = CharacterLocationsData.fromJsonString(json)
    fun serializeLocations(data: CharacterLocationsData): String = data.toJson().toString()

    fun deserializeCustomFields(json: String): List<CharacterCustomField> = CharacterCustomField.parseList(json)
    fun serializeCustomFields(list: List<CharacterCustomField>): String = CharacterCustomField.serializeList(list)
}
