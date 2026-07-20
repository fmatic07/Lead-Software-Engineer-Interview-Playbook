# Lead Software Engineer Interview Playbook

> A senior-level engineering handbook for Staff-track interviews at enterprise Java organizations.

---

## Purpose

This repository is a **structured interview preparation system** — not a cheat sheet, not a trivia bank. It is designed to help you articulate **how you think, decide, and lead** under the scrutiny of senior hiring panels at companies such as ING, Globe Telecom, Deltek, ReciMe, Maya, GovTech Singapore, BJAK, Canva, Atlassian, Grab, and comparable enterprise Java shops.

You will prepare for roles at the **Senior Software Engineer → Lead Software Engineer → Technical Lead → Software Architect** spectrum. Content spans the full stack you operate in daily:

| Domain | Technologies |
|--------|-------------|
| Backend | Java, Spring Boot, Spring Security, Hibernate, JPA |
| Data | MySQL, PostgreSQL |
| Integration | REST APIs, JWT, Microservices |
| Infrastructure | Docker, AWS, CI/CD |
| Frontend (full-stack depth) | React, Next.js |

Every section is a workbook. Fill in the **Notes** sections with your own production war stories, architectural decisions, and measured outcomes. That is what separates a memorable candidate from a generic one.

---

## Target Interview Level

| Level | What Panels Evaluate |
|-------|---------------------|
| **Senior Software Engineer** | Deep technical mastery, ownership of complex features, code quality, mentoring juniors |
| **Lead Software Engineer** | Technical direction within a team, cross-module design, delivery under constraints |
| **Technical Lead** | Architecture within a domain, stakeholder communication, unblocking teams |
| **Software Architect** | System-wide design, technology strategy, tradeoff reasoning, organizational impact |

If you are preparing for a **Staff+ or Principal** loop, treat this playbook as the foundation and extend System Design and Architecture sections with org-scale scenarios.

---

## Learning Philosophy

This playbook is built on principles that enterprise panels actually probe — not LeetCode grinding, not framework trivia in isolation.

1. **Real engineering decisions** — Every answer should trace back to a decision you made, alternatives you rejected, and why.
2. **Architecture thinking** — Move from "how does X work?" to "when would I choose X over Y, and at what cost?"
3. **Leadership** — Demonstrate influence, mentorship, and accountability without needing a manager title on your badge.
4. **Communication** — Structure answers for clarity: context → constraint → decision → outcome → lesson.
5. **System design** — Design for production realities: failure modes, observability, data consistency, and operational burden.
6. **Tradeoffs** — There is no perfect architecture. Show that you know what you are giving up.
7. **Production experience** — Incidents resolved, performance regressions caught, deployments stabilized — these are your credentials.

> **Rule of thumb:** If you cannot tie a topic to something you shipped, debugged, or decided in production, you are not ready to discuss it in a senior loop.

---

## How to Study

### Daily Rhythm (recommended: 2–3 focused hours)

| Block | Duration | Activity |
|-------|----------|----------|
| **Read** | 30 min | Review topics in the current section. Do not skip the Purpose — it frames what interviewers are actually testing. |
| **Reflect** | 45 min | For each topic, write one production example in the Notes section. Use metrics where possible. |
| **Practice** | 45 min | Explain the topic aloud as if to a hiring manager. Record yourself or use a mock partner. |
| **Check** | 15 min | Mark the Progress Checklist honestly. Unchecked items carry forward. |

### Weekly Rhythm

- **End of week:** Complete one mock interview session (see [09-Mock-Interviews](./09-Mock-Interviews/)).
- **End of week:** Update [08-Company-Playbooks](./08-Company-Playbooks/) with research on your target company.
- **Continuous:** Refine Personal Brand narratives — they should evolve as you practice.

### What Not to Do

- Do not memorize framework APIs without understanding the problem they solve.
- Do not study sections in random order — the sequence below builds on prior sections.
- Do not mark checklists complete without being able to explain tradeoffs cold.

---

## Recommended Study Order

Follow this sequence. Each phase builds context for the next.

| Phase | Days | Sections | Focus |
|-------|------|----------|-------|
| **1 — Foundation** | Day 1 | [01-Personal-Brand](./01-Personal-Brand/), [06-Behavioral](./06-Behavioral/) | Narrative, story library, behavioral frameworks, communication baseline |
| **2 — Core Technical** | Days 2–3 | [02-Java](./02-Java/), [03-Spring-Boot](./03-Spring-Boot/) | Language depth, framework mastery, production patterns |
| **3 — Systems Thinking** | Days 4–5 | [04-System-Design](./04-System-Design/), [07-Architecture](./07-Architecture/) | Scalability drills + architecture judgment, DDD, production storytelling |
| **4 — Leadership & Judgment** | Day 6 | [05-Leadership](./05-Leadership/) | Technical direction, mentoring, decision-making |
| **5 — Company Targeting** | Day 7 | [08-Company-Playbooks](./08-Company-Playbooks/) | Company-specific culture, stack, and interview format |
| **6 — Integration & Pressure** | Days 8–9 | [09-Mock-Interviews](./09-Mock-Interviews/) + review weak areas | Full-loop simulation, feedback incorporation |
| **7 — Close & Negotiate** | Day 10 | [10-Career-Strategy](./10-Career-Strategy/) + final review | Career planning, negotiation, offer evaluation, resignation, first 90 days |

---

## Expected Completion Timeline

**Total: 10 days** at 2–3 focused hours per day (~25–30 hours).

| Day | Goal | Exit Criteria |
|-----|------|---------------|
| 1 | Personal narrative locked | Can deliver Tell-Me-About-Yourself in 2 min; 5 STAR stories drafted |
| 2 | Java depth | Can explain concurrency, JVM memory, and Java 21 features with examples |
| 3 | Spring ecosystem | Can whiteboard a secured REST API with JPA and discuss testing strategy |
| 4 | System design fundamentals | Can design a URL shortener and a rate-limited API with tradeoff analysis |
| 5 | Architecture & engineering design | Can defend style/DDD/microservices tradeoffs, walk a case study, and narrate a production architecture decision (cloud/CI/CD supplements as needed) |
| 6 | Leadership stories | Can articulate 3 decisions where you led without authority |
| 7 | Company research | At least 2 target company playbooks completed |
| 8 | Mock interview #1 | Completed timed session; feedback logged |
| 9 | Mock interview #2 + gap fill | Weak areas revisited; checklists updated |
| 10 | Career strategy ready | Comp research done; offer scorecard ready; 30-60-90 drafted; full repository review complete |

Adjust pacing if you are interviewing on a shorter timeline — prioritize **Personal Brand**, **System Design**, and **Company Playbooks** first.

---

## Repository Map

| # | Section | Description |
|---|---------|-------------|
| 01 | [Personal Brand](./01-Personal-Brand/) | Career narrative, positioning, strengths, growth areas |
| 02 | [Java](./02-Java/) | JVM, concurrency, collections, Java 21, patterns, performance |
| 03 | [Spring Boot](./03-Spring-Boot/) | Spring Boot, Security, JWT, JPA/Hibernate, REST, testing, microservices |
| 04 | [System Design](./04-System-Design/) | Scalability, storage, caching, messaging, design exercises |
| 05 | [Leadership](./05-Leadership/) | Technical leadership, mentoring, cross-functional work, decision-making |
| 06 | [Behavioral](./06-Behavioral/) | Behavioral mastery: foundations, career, motivation, collaboration, conflict, leadership stories, innovation, product thinking, 110-question library, personal story bank |
| 07 | [Architecture](./07-Architecture/) | Software Architecture & Engineering Design — principles, styles, DDD, microservices, integration, APIs, data architecture, decision-making, case studies, interview questions, production storytelling (plus cloud/CI/CD/frontend integration supplements) |
| 08 | [Company Playbooks](./08-Company-Playbooks/) | ING, Globe, Deltek, Maya, GovTech SG, ReciMe, BJAK, Canva, Atlassian, Grab + Startup/Enterprise/Banking/Government guides |
| 09 | [Mock Interviews](./09-Mock-Interviews/) | Interview bootcamp: day guide, HR→final scripts, system design drills, scorecards |
| 10 | [Career Strategy](./10-Career-Strategy/) | Career planning, recruiter comms, negotiation, offers, resignation, background checks, 30-60-90, credibility, growth, reflection |

---

## Getting Started

1. Clone this repository and work through sections in order.
2. Open today's section and read the **Purpose** of each document.
3. Fill in **Notes** with your real examples as you go — empty notes mean unprepared answers.
4. Mark **Progress Checklists** only when you can defend your answers under follow-up questions.
5. Run at least two full mock interviews before your first real loop.

---

*Built for engineers who ship production systems — not for those collecting frameworks on a résumé.*
# Lead-Software-Engineer-Interview-Playbook
