# Refactoring Plan (MNIST-kotlin)

## Objectives
- Maintain CLI/train and server/inference while improving readability, reusability, testability, and deployment reliability.
- Eliminate hardcoded network architecture; build network layers dynamically from JSON model spec (weights + layer metadata).
- Centralize constants/configuration and remove duplicated magic numbers (e.g., 784).

## Non-Goals (initial)
- Major algorithmic changes (e.g., new optimizers) beyond interfaces needed for IO/DI.
- Performance micro-optimizations unless required by refactor.

## Deliverables
- JSON ModelSpec v1 that encodes architecture + parameters and can fully rebuild a `Network` without hardcoded layers.
- Modular server with health/version endpoints and environment-driven model path.
- Minimal unit tests for math/NN core and model IO round-trip.
- Clean constants/config, fixed typos, and reduced coupling across packages.

---

## Work Breakdown (Phases)

### Phase 1: Constants & Naming (quick wins)
- Unify MNIST sizes and names
  - Fix typo: `COL_LENTGH` → `COL_LENGTH` in `MnistConst` and references.
  - Add `MNIST_INPUT_SIZE = ROW_LENGTH * COL_LENGTH`; replace all `784` literals.
  - Prefer `MnistConst.MNIST_INPUT_SIZE` in server, trainer, services.
- Expand `TrainingConfig`
  - Add `inputSize`, `outputSize` (default 784, 10) to avoid duplication in network builders.
- Configuration
  - Add env var `MODEL_PATH` (default `mnist_model.json`), and centralize path resolution.

### Phase 2: Model Serialization v1 (JSON-driven build)
- Define a typed spec
  - `data class ModelSpec(val version: String, val layers: List<LayerSpec>)`
  - `sealed interface LayerSpec { val type: String }`
  - `data class DenseSpec(val inputSize:Int, val outputSize:Int, val weights: DoubleArray, val biases: DoubleArray): LayerSpec { override val type = "Dense" }`
  - `data class ActivationSpec(val name: String): LayerSpec { override val type = name /* e.g., ReLU, Softmax, Sigmoid */ }`
- Implement `LayerFactory`
  - Build `Layer` instances from `LayerSpec` list in order; validate adjacency shapes.
- Update saver/loader
  - `ModelSaver`: emit `ModelSpec(version = "1")` with ordered `LayerSpec`s. For activations, only `ActivationSpec(name)`.
  - `ModelLoader`: read `ModelSpec`; build the `Network` fresh via `LayerFactory` (do not rely on prebuilt architecture). Provide clear validation errors.
  - Backward compatibility: if legacy list-of-maps is detected, attempt to parse and wrap into `ModelSpec` (best-effort), or fail with actionable error.
- Validation
  - Verify layer count/types and matrix/vector shapes between consecutive layers.

### Phase 3: Server Modularization
- Extract `Application.module()` to enable `testApplication` in tests.
- Endpoints
  - Keep `/api/predict` as-is; add `/healthz` (200 OK) and `/version` (ModelSpec version + commit SHA if available).
- Logging
  - Replace println with `slf4j` logger; INFO on startup, WARN on model-missing.
- Model path
  - Resolve from `MODEL_PATH` env or CLI args; if missing, start with random weights + clear warning.

### Phase 4: Dataset Abstraction
- Introduce `DatasetProvider`
  - `interface DatasetProvider { fun all(): List<DataPair>; fun split(trainRatio:Double=0.8): Pair<List<DataPair>, List<DataPair>> }`
  - Move MNIST-specific normalization (255.0) and one-hot to a `Preprocessor` component; avoid duplicating numeric constants.
- Rename to clarify
  - `MnistDatasetService` → `MnistDatasetProvider` (or keep name but depend on interfaces in services).
- Update `MnistLearningService` to depend on `DatasetProvider` via constructor to reduce MNIST coupling.

### Phase 5: Tests (minimal safety net)
- Math/NN core
  - `DenseMatrix`: `apply/mul/transpose/add/subtract` dimension checks.
  - `DenseVector`: `dot/outerProduct/normalize/scale` behaviors.
  - `Softmax.forward/backward` numerical properties; `CrossEntropy.forward/backward` gradients (sanity checks).
  - `Dense.backward` gradient shape tests.
- Model IO
  - Round-trip: build small network → save → load to new instance via `ModelSpec` → predictions equal within tolerance.
- Server (optional minimal)
  - Using `testApplication`: `/healthz` 200, `/api/predict` happy-path and validation error when image size != input size.

### Phase 6: Build, Docker, CI
- Dockerfile
  - Do not `COPY` model file; accept `MODEL_PATH` at runtime (mount or bake via env).
  - Run as non-root user; keep base small; expose `8080`.
- Cloud Build/.gcloudignore
  - Exclude large MNIST dataset files from build context; document model artifact strategy (Artifact Registry, GCS, or mounted volume).
- Gradle/CI
  - Ensure `:app:check` runs tests. Consider adding formatter (ktlint or spotless) later.

### Phase 7: Examples/Plots tidy-up
- Parameterize output dir for `LossPlotter` (default `lets-plot-images/`), avoid scattering images.
- Optionally move `example` package to a separate sourceSet or exclude from production jar.

---

## Acceptance Criteria
- Loading a JSON ModelSpec fully reconstructs the network (layers and parameters) without hardcoded architecture.
- No direct `784` literals; input/output sizes resolve via constants/config.
- Server starts without bundled model when `MODEL_PATH` is unset; logs a clear warning; `/healthz` returns 200.
- Model IO round-trip test passes with consistent predictions.
- Typos fixed and logging standardized.

## Risks & Mitigations
- Legacy JSON incompatibility → backward-compat parser + clear migration doc.
- Shape mismatches on load → strict validation with actionable error messages.
- Test flakiness due to randomness → seed control in `TrainingConfig` and deterministic tests.

## Migration Notes
- If existing `mnist_model.json` follows the legacy array-of-maps format, provide a one-time converter or fallback loader.
- Document how to provide `MODEL_PATH` (env/arg) in README/DEPLOY.

## Suggested Order of Implementation
1) Phase 1 (constants/naming) → 2) Phase 2 (ModelSpec + LayerFactory + saver/loader) → 3) Phase 3 (Ktor module + endpoints) → 5) Tests → 6) Docker/CI → 4/7) Cleanup.

