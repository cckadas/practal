# Achievement System Implementation

### Models

#### Achievement (Domain Model)
`domain/model/Achievement.kt`
```kotlin
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val category: String, // practice/challenge/social/technique/special
    val requirement: Int,
    val xpReward: Int
)
```

#### UserAchievement (Domain Model)
`domain/model/UserAchievement.kt`
```kotlin
data class UserAchievement(
    val userId: String,
    val achievementId: String,
    val unlockedDate: Timestamp,
    val progress: Int,
    val completed: Boolean
)
```

#### AchievementUI (UI Model)
`ui/screens/AchievementsScreen.kt`
```kotlin
data class AchievementUI(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val category: String,
    val unlockedDate: String?,
    val xpReward: Int
)
```

### ViewModel

#### AchievementViewModel
`domain/controller/AchievementViewModel.kt`

**Functions:**
- `loadAchievements()` - loads all available achievements from Firestore
- `loadUserAchievements(userId)` - loads user's achievement progress
- `checkAndUnlockAchievements(user)` - checks user stats and unlocks achievements
- `setupDefaultAchievements()` - creates default achievements on first run

**Achievement Categories:**
1. **Practice Hours** - based on `totalPracticeHours`
2. **Streaks** - based on `currentStreak`
3. **Challenges** - based on `challenges.size`
4. **Community** - based on `followers.size` and `following.size`
5. **Special** - time-based achievements (night owl, early bird)

## Default Achievements

### Practice Hours
tracks: `User.totalPracticeHours`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| First Steps | `practice_10` | 🎵 | 10 hours | Practice for 10 hours | 50 |
| Dedicated Musician | `practice_50` | 🎼 | 50 hours | Practice for 50 hours | 100 |
| Century Practice | `practice_100` | 🎯 | 100 hours | Practice for 100 hours | 200 |
| Practice Master | `practice_250` | ⭐ | 250 hours | Practice for 250 hours | 500 |
| Half Thousand | `practice_500` | 💎 | 500 hours | Practice for 500 hours | 1000 |
| Practice Legend | `practice_1000` | 👑 | 1000 hours | Practice for 1000 hours | 2000 |

### Streaks
tracks: `User.currentStreak`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| Week Warrior | `streak_7` | 🔥 | 7 days | Maintain a 7-day practice streak | 50 |
| 30-Day Champion | `streak_30` | 💪 | 30 days | Maintain a 30-day practice streak | 150 |
| Two Month Master | `streak_60` | 🏆 | 60 days | Maintain a 60-day practice streak | 300 |
| Hundred Day Hero | `streak_100` | 🌟 | 100 days | Maintain a 100-day practice streak | 500 |
| Year-Long Dedication | `streak_365` | 👑 | 365 days | Maintain a 365-day practice streak | 2000 |

### Challenges
tracks: `User.challenges.size`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| Challenge Accepted | `challenge_1` | ✅ | 1 challenge | Complete your first challenge | 50 |
| Challenge Enthusiast | `challenge_5` | 🎖️ | 5 challenges | Complete 5 challenges | 100 |
| Challenge Master | `challenge_10` | 🏆 | 10 challenges | Complete 10 challenges | 200 |
| Challenge Champion | `challenge_25` | ⭐ | 25 challenges | Complete 25 challenges | 500 |
| Challenge Legend | `challenge_50` | 👑 | 50 challenges | Complete 50 challenges | 1000 |

### Community - Followers
tracks: `User.followers.size`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| Rising Star | `followers_10` | 🌠 | 10 followers | Gain 10 followers | 50 |
| Popular Musician | `followers_50` | ⭐ | 50 followers | Gain 50 followers | 150 |
| Community Star | `followers_100` | ✨ | 100 followers | Gain 100 followers | 300 |
| Influencer | `followers_500` | 👑 | 500 followers | Gain 500 followers | 1000 |

### Community - Following
tracks: `User.following.size`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| Social Butterfly | `following_10` | 🦋 | 10 following | Follow 10 musicians | 30 |
| Community Builder | `following_50` | 🤝 | 50 following | Follow 50 musicians | 100 |

### Special
tracks: `User.practiceSessions.endTime`

| Achievement | ID | Icon | Requirement | Description | XP Reward |
|------------|-----|------|-------------|-------------|-----------|
| Night Owl | `night_owl` | 🦉 | 10 sessions | Practice after midnight 10 times | 100 |
| Early Bird | `early_bird` | 🌅 | 10 sessions | Practice before 6 AM 10 times | 100 |

## Integrations

### 1. Practice Session Completion
**file:** `LogPracticeSessionActivity.kt`

after completing a practice session:
```kotlin
loggedInUser?.let { user ->
    achievementViewModel.checkAndUnlockAchievements(user)
}
```
automatically checks:
- Practice hours milestones
- Streak achievements
- Updates progress for incomplete achievements

### 2. Challenge Completion
integrated in challenge completion logic:
```kotlin
achievementViewModel.checkAndUnlockAchievements(user)
```

### 3. Profile Updates
user gains followers/following:
```kotlin
achievementViewModel.checkAndUnlockAchievements(user)
```

### 4. UI Display

#### AchievementsScreen
full achievement list
- category filtering
- progress tracking
- unlock status
- XP rewards

#### UserProfileScreen
recent achievements section
- last 6 unlocked achievements
- badge display
- link to full achievements screen

## Firebase Structure

### Collections

#### achievements
```
achievements/
  ├── practice_10/
  │   ├── id: "practice_10"
  │   ├── title: "First Steps"
  │   ├── description: "Practice for 10 hours"
  │   ├── badgeIcon: "🎵"
  │   ├── category: "practice"
  │   ├── requirement: 10
  │   └── xpReward: 50
  └── ...
```

#### userAchievements
```
userAchievements/
  ├── {userId}_{achievementId}/
  │   ├── userId: "12345"
  │   ├── achievementId: "practice_10"
  │   ├── unlockedDate: Timestamp
  │   ├── progress: 100
  │   └── completed: true
  └── ...
```
