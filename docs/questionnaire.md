# MindGuard AI — Questionnaire Taxonomy & Feature Schema

## 1. Structure & UX Design
The mental health risk assessment consists of **36 structured parameters** grouped into 6 logical categories. To optimize cognitive load and avoid survey fatigue, questions are presented category-by-category (6 questions per page).

---

## 2. Parameter Categories & Indicators

### Category A: Mood and Emotional State (`mood_`)
- `mood_01`: Persistent feelings of sadness or low mood.
- `mood_02`: Loss of interest or pleasure in daily activities (Anhedonia).
- `mood_03`: Feelings of worthlessness or excessive guilt.
- `mood_04`: Emotional exhaustion and lack of vitality.
- `mood_05`: Frequent mood fluctuations and emotional volatility.
- `mood_06`: Feelings of irritability or frustration over minor issues.

### Category B: Anxiety and Worry (`anxiety_`)
- `anxiety_01`: Excessive, uncontrollable worry about daily events.
- `anxiety_02`: Inner restlessness or feeling on edge.
- `anxiety_03`: Physical tension, trembling, or rapid heartbeat.
- `anxiety_04`: Sudden episodes of intense fear or panic.
- `anxiety_05`: Anticipatory dread of future uncertainties.
- `anxiety_06`: Difficulty calming mind when stressed.

### Category C: Stress & Coping (`stress_`)
- `stress_01`: Perceived overload from work, academic, or personal responsibilities.
- `stress_02`: Inability to relax or unwind during free time.
- `stress_03`: Feeling overwhelmed by simultaneous demands.
- `stress_04`: Reduced tolerance to frustration or changes.
- `stress_05`: Feeling that difficulties are piling up too high to overcome.
- `stress_06`: Physical symptoms of tension (headaches, muscle tightness).

### Category D: Sleep and Recovery (`sleep_`)
- `sleep_01`: Difficulty falling asleep (Sleep latency > 30 min).
- `sleep_02`: Frequent mid-night awakenings with difficulty returning to sleep.
- `sleep_03`: Early morning awakenings without feeling refreshed.
- `sleep_04`: Total sleep duration (< 6 hours or > 9 hours consistently).
- `sleep_05`: Daytime grogginess, lethargy, or fatigue.
- `sleep_06`: Irregular sleep-wake schedule across weekdays and weekends.

### Category E: Cognitive Functioning (`cog_`)
- `cog_01`: Difficulty maintaining focus on tasks or reading.
- `cog_02`: Forgetfulness regarding everyday appointments or obligations.
- `cog_03`: Difficulty making decisions, even minor ones.
- `cog_04`: Racing or intrusive thoughts impairing clarity.
- `cog_05`: Slower processing speed or mental fog.
- `cog_06`: Noticeable drop in personal productivity or efficacy.

### Category F: Daily Functioning & Social Wellbeing (`social_`)
- `social_01`: Difficulty starting or finishing routine daily chores.
- `social_02`: Tendency to withdraw from friends, family, or social gatherings.
- `social_03`: Persistent feelings of loneliness or isolation.
- `social_04`: Interpersonal friction or relationship strain.
- `social_05`: Neglect of personal hygiene, nutrition, or physical self-care.
- `social_06`: Lack of motivation or purpose in daily routine.

---

## 3. Standard Likert Scale Mapping
Every standard item uses a 5-point Likert scale normalized to numerical values for ML ingestion:

| Response | Numeric Value | Normalized (0.0 – 1.0) |
|---|---|---|
| Never | 0 | 0.00 |
| Rarely | 1 | 0.25 |
| Sometimes | 2 | 0.50 |
| Often | 3 | 0.75 |
| Almost Always | 4 | 1.00 |
