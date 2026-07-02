# Locator Cache

This file explains the locator cache used by `SelfHealingLocators.java`.

## What It Does

Sometimes Selenium cannot find an element because the locator is old or wrong.

When that happens, the framework can ask the LLM for a better locator. If the new locator works, it saves that locator in this file:

```text
target/locator_cache.json
```

Next time the same locator fails, the framework checks the cache first. This avoids calling the LLM again for the same problem.

## Simple Flow

1. Try the original Selenium locator.
2. If it fails, check the saved cache.
3. If the cache has a working locator, use it.
4. If the cache does not help, ask the LLM for a new locator.
5. Save the working locator for future test runs.

## Why This Helps

- Tests run faster after a locator has already been fixed once.
- Fewer LLM/API calls are needed.
- Fixed locators can be reused in later test runs.

## When To Delete The Cache

Delete this file if you want the framework to ask the LLM again:

```text
target/locator_cache.json
```

This is useful when the app UI changes again or the saved locator is no longer correct.

## Example

```json
{
  "By.id: oldLoginButton": {
    "type": "xpath",
    "value": "//button[@id='login']"
  }
}
```

In this example, Selenium first tried `oldLoginButton`. The framework found a better XPath and saved it for next time.
