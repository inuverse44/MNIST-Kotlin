document.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');
    const clearBtn = document.getElementById('clearBtn');
    const predictBtn = document.getElementById('predictBtn');
    const predictionEl = document.getElementById('prediction');
    const confidenceEl = document.getElementById('confidence');
    const barsContainer = document.getElementById('bars');
    const startTrainBtn = document.getElementById('startTrainBtn');
    const trainStatusEl = document.getElementById('trainStatus');
    const epochsInput = document.getElementById('epochs');
    const lrInput = document.getElementById('learningRate');
    const trainSizeInput = document.getElementById('trainSize');
    const testSizeInput = document.getElementById('testSize');
    const hiddenSizeInput = document.getElementById('hiddenSize');

    // Layer builder elements
    const layerListEl = document.getElementById('layerList');
    const addDenseBtn = document.getElementById('addDenseBtn');
    const addReLUBtn = document.getElementById('addReLUBtn');
    const addSigmoidBtn = document.getElementById('addSigmoidBtn');
    const addSoftmaxBtn = document.getElementById('addSoftmaxBtn');
    const moveUpBtn = document.getElementById('moveUpBtn');
    const moveDownBtn = document.getElementById('moveDownBtn');
    const removeBtn = document.getElementById('removeBtn');
    const resetDefaultBtn = document.getElementById('resetDefaultBtn');

    // Layer model (UI state)
    let layers = [];
    let selectedIndex = -1;

    function setDefaultLayers() {
        layers = [
            { type: 'Dense', inputSize: 28 * 28, outputSize: Number(hiddenSizeInput.value) || 100 },
            { type: 'ReLU' },
            { type: 'Dense', inputSize: Number(hiddenSizeInput.value) || 100, outputSize: 10 },
            { type: 'Softmax' }
        ];
        selectedIndex = -1;
        renderLayers();
    }

    function renderLayers() {
        layerListEl.innerHTML = '';
        layers.forEach((layer, idx) => {
            const row = document.createElement('div');
            row.className = 'layer-row' + (idx === selectedIndex ? ' selected' : '');
            row.addEventListener('click', () => { selectedIndex = idx; renderLayers(); });

            const typeSpan = document.createElement('span');
            typeSpan.className = 'layer-type';
            typeSpan.textContent = `${idx}. ${layer.type}`;
            row.appendChild(typeSpan);

            if (layer.type === 'Dense') {
                const inInput = document.createElement('input');
                inInput.type = 'number';
                inInput.min = '1';
                inInput.value = layer.inputSize;
                inInput.title = 'inputSize';
                inInput.addEventListener('change', (e) => {
                    layer.inputSize = Number(e.target.value);
                });
                row.appendChild(inInput);

                const arrow = document.createElement('span');
                arrow.textContent = ' → ';
                row.appendChild(arrow);

                const outInput = document.createElement('input');
                outInput.type = 'number';
                outInput.min = '1';
                outInput.value = layer.outputSize;
                outInput.title = 'outputSize';
                outInput.addEventListener('change', (e) => {
                    layer.outputSize = Number(e.target.value);
                    // propagate to next Dense inputSize if present
                    const next = layers[idx + 1];
                    if (next && next.type === 'Dense') {
                        next.inputSize = layer.outputSize;
                        // keep UI in sync
                        renderLayers();
                    }
                });
                row.appendChild(outInput);
            }

            layerListEl.appendChild(row);
        });
    }

    // Layer builder controls
    addDenseBtn?.addEventListener('click', () => {
        const prev = layers[selectedIndex >= 0 ? selectedIndex : layers.length - 1];
        const defaultIn = prev && prev.type === 'Dense' ? prev.outputSize : (layers.length === 0 ? 28 * 28 : 10);
        layers.splice(selectedIndex >= 0 ? selectedIndex + 1 : layers.length, 0, { type: 'Dense', inputSize: defaultIn, outputSize: 100 });
        selectedIndex = (selectedIndex >= 0 ? selectedIndex + 1 : layers.length - 1);
        renderLayers();
    });
    addReLUBtn?.addEventListener('click', () => { addAct('ReLU'); });
    addSigmoidBtn?.addEventListener('click', () => { addAct('Sigmoid'); });
    addSoftmaxBtn?.addEventListener('click', () => { addAct('Softmax'); });
    function addAct(name) {
        layers.splice(selectedIndex >= 0 ? selectedIndex + 1 : layers.length, 0, { type: name });
        selectedIndex = (selectedIndex >= 0 ? selectedIndex + 1 : layers.length - 1);
        renderLayers();
    }
    moveUpBtn?.addEventListener('click', () => {
        if (selectedIndex > 0) {
            const tmp = layers[selectedIndex - 1];
            layers[selectedIndex - 1] = layers[selectedIndex];
            layers[selectedIndex] = tmp;
            selectedIndex--;
            renderLayers();
        }
    });
    moveDownBtn?.addEventListener('click', () => {
        if (selectedIndex >= 0 && selectedIndex < layers.length - 1) {
            const tmp = layers[selectedIndex + 1];
            layers[selectedIndex + 1] = layers[selectedIndex];
            layers[selectedIndex] = tmp;
            selectedIndex++;
            renderLayers();
        }
    });
    removeBtn?.addEventListener('click', () => {
        if (selectedIndex >= 0) {
            layers.splice(selectedIndex, 1);
            selectedIndex = -1;
            renderLayers();
        }
    });
    resetDefaultBtn?.addEventListener('click', () => setDefaultLayers());

    // Initialize layer builder
    setDefaultLayers();

    let isDrawing = false;

    // Init probability bars
    for (let i = 0; i < 10; i++) {
        const wrapper = document.createElement('div');
        wrapper.className = 'bar-wrapper';
        wrapper.innerHTML = `
            <div class="bar-value" id="val-${i}">0%</div>
            <div class="bar" style="height: 2%" id="bar-${i}"></div>
            <div class="bar-label">${i}</div>
        `;
        barsContainer.appendChild(wrapper);
    }

    // Canvas settings
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.lineWidth = 18;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.strokeStyle = 'white';

    function getPos(e) {
        const rect = canvas.getBoundingClientRect();
        const clientX = e.touches ? e.touches[0].clientX : e.clientX;
        const clientY = e.touches ? e.touches[0].clientY : e.clientY;
        
        // Scale factor between CSS and Canvas pixels
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;
        
        return {
            x: (clientX - rect.left) * scaleX,
            y: (clientY - rect.top) * scaleY
        };
    }

    function startDraw(e) {
        e.preventDefault();
        isDrawing = true;
        const pos = getPos(e);
        ctx.beginPath();
        ctx.moveTo(pos.x, pos.y);
    }

    function draw(e) {
        e.preventDefault();
        if (!isDrawing) return;
        const pos = getPos(e);
        ctx.lineTo(pos.x, pos.y);
        ctx.stroke();
    }

    function stopDraw() {
        isDrawing = false;
    }

    // Desktop
    canvas.addEventListener('mousedown', startDraw);
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('mouseup', stopDraw);
    canvas.addEventListener('mouseout', stopDraw);
    
    // Mobile
    canvas.addEventListener('touchstart', startDraw);
    canvas.addEventListener('touchmove', draw);
    canvas.addEventListener('touchend', stopDraw);

    clearBtn.addEventListener('click', () => {
        ctx.fillStyle = 'black';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        predictionEl.textContent = '-';
        confidenceEl.textContent = '';
        
        // Reset bars
        document.querySelectorAll('.bar').forEach(b => {
            b.style.height = '2%';
            b.classList.remove('active');
        });
        document.querySelectorAll('.bar-value').forEach(v => {
            v.style.opacity = '0';
        });
    });

    // ---- Training ----
    startTrainBtn?.addEventListener('click', async () => {
        const params = {
            layers: layers.map(l => ({
                type: l.type,
                ...(l.type === 'Dense' ? { inputSize: Number(l.inputSize), outputSize: Number(l.outputSize) } : {})
            })),
            config: {
                epochs: Number(epochsInput.value),
                learningRate: Number(lrInput.value),
                trainSize: Number(trainSizeInput.value),
                testSize: Number(testSizeInput.value),
                hiddenLayerSize: Number(hiddenSizeInput.value)
            }
        };
        try {
            trainStatusEl.textContent = 'Starting training...';
            const res = await fetch('/api/train/start', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(params)
            });
            const { jobId } = await res.json();
            streamTrainStatus(jobId);
        } catch (e) {
            trainStatusEl.textContent = 'Failed to start training: ' + e.message;
        }
    });

    async function pollTrainStatus(jobId) {
        const interval = setInterval(async () => {
            try {
                const res = await fetch(`/api/train/status?jobId=${encodeURIComponent(jobId)}`);
                const s = await res.json();
                if (s.error) {
                    trainStatusEl.textContent = 'Error: ' + s.error;
                    clearInterval(interval);
                    return;
                }
                trainStatusEl.textContent = JSON.stringify(s, null, 2);
                if (s.state === 'completed' || s.state === 'failed') {
                    clearInterval(interval);
                }
            } catch (e) {
                trainStatusEl.textContent = 'Status error: ' + e.message;
                clearInterval(interval);
            }
        }, 1500);
    }

    function streamTrainStatus(jobId) {
        if (!window.EventSource) {
            // Fallback
            pollTrainStatus(jobId);
            return;
        }
        const es = new EventSource(`/api/train/stream?jobId=${encodeURIComponent(jobId)}`);
        es.onmessage = (ev) => {
            try {
                const data = JSON.parse(ev.data);
                trainStatusEl.textContent = JSON.stringify(data, null, 2);
                if (data.state === 'completed' || data.state === 'failed') {
                    es.close();
                }
            } catch (e) {
                console.error('SSE parse error', e);
            }
        };
        es.onerror = () => {
            es.close();
            // Try fallback polling if SSE fails
            pollTrainStatus(jobId);
        };
    }

    predictBtn.addEventListener('click', async () => {
        const imageData = getMnistData();
        
        try {
            const response = await fetch('/api/predict', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ image: imageData })
            });
            
            if (!response.ok) throw new Error('Network response was not ok');
            
            const result = await response.json();
            displayResult(result);
        } catch (error) {
            console.error('Error:', error);
            predictionEl.textContent = 'Error';
        }
    });

    function getMnistData() {
        const tempCanvas = document.createElement('canvas');
        tempCanvas.width = 28;
        tempCanvas.height = 28;
        const tempCtx = tempCanvas.getContext('2d');
        
        // Center of mass or bounding box normalization could be added here for better accuracy
        tempCtx.drawImage(canvas, 0, 0, 28, 28);
        
        const imgData = tempCtx.getImageData(0, 0, 28, 28);
        const data = imgData.data;
        const input = [];
        
        for (let i = 0; i < data.length; i += 4) {
            // Take the R channel (it's grayscale white-on-black)
            input.push(data[i] / 255.0);
        }
        
        return input;
    }

    function displayResult(result) {
        predictionEl.textContent = result.digit;
        
        result.probabilities.forEach((prob, i) => {
            const bar = document.getElementById(`bar-${i}`);
            const val = document.getElementById(`val-${i}`);
            
            // Format percentage (e.g., "98%")
            // If < 1%, show empty or "<1"
            const percentage = (prob * 100).toFixed(0);
            val.textContent = percentage + '%';
            
            // Show value only if meaningful probability
            val.style.opacity = prob > 0.01 ? '1' : '0';

            const height = Math.max(2, prob * 100); // Min height 2%
            bar.style.height = `${height}%`;
            
            if (i === result.digit) {
                bar.classList.add('active');
                val.style.fontWeight = 'bold';
                val.style.color = '#2196F3';
            } else {
                bar.classList.remove('active');
                val.style.fontWeight = 'normal';
                val.style.color = '#555';
            }
        });
        
        const maxProb = result.probabilities[result.digit];
        confidenceEl.textContent = `Confidence: ${(maxProb * 100).toFixed(1)}%`;
    }
});
