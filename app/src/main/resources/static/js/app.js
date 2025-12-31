/**
 * MNIST Kotlin Frontend Application
 * Uses Chart.js for visualization.
 */

document.addEventListener('DOMContentLoaded', () => {
    
    // --- Components ---

    /**
     * Handles Canvas interactions.
     */
    class CanvasHandler {
        constructor(canvasId, clearBtnId) {
            this.canvas = document.getElementById(canvasId);
            this.ctx = this.canvas.getContext('2d', { willReadFrequently: true });
            this.clearBtn = document.getElementById(clearBtnId);
            this.isDrawing = false;
            this.init();
        }

        init() {
            this.ctx.fillStyle = 'black';
            this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
            this.ctx.lineCap = 'round';
            this.ctx.lineJoin = 'round';
            this.ctx.strokeStyle = 'white';
            this.ctx.lineWidth = 18;

            const start = this.startDraw.bind(this);
            const move = this.draw.bind(this);
            const stop = this.stopDraw.bind(this);

            this.canvas.addEventListener('mousedown', start);
            this.canvas.addEventListener('mousemove', move);
            this.canvas.addEventListener('mouseup', stop);
            this.canvas.addEventListener('mouseout', stop);

            this.canvas.addEventListener('touchstart', start, { passive: false });
            this.canvas.addEventListener('touchmove', move, { passive: false });
            this.canvas.addEventListener('touchend', stop);

            this.clearBtn?.addEventListener('click', this.clear.bind(this));
        }

        getPos(e) {
            const rect = this.canvas.getBoundingClientRect();
            const clientX = e.touches ? e.touches[0].clientX : e.clientX;
            const clientY = e.touches ? e.touches[0].clientY : e.clientY;
            const scaleX = this.canvas.width / rect.width;
            const scaleY = this.canvas.height / rect.height;
            return { x: (clientX - rect.left) * scaleX, y: (clientY - rect.top) * scaleY };
        }

        startDraw(e) {
            if (e.cancelable) e.preventDefault();
            this.isDrawing = true;
            const pos = this.getPos(e);
            this.ctx.beginPath();
            this.ctx.moveTo(pos.x, pos.y);
        }

        draw(e) {
            if (e.cancelable) e.preventDefault();
            if (!this.isDrawing) return;
            const pos = this.getPos(e);
            this.ctx.lineTo(pos.x, pos.y);
            this.ctx.stroke();
        }

        stopDraw() {
            this.isDrawing = false;
        }

        clear() {
            this.ctx.fillStyle = 'black';
            this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        }

        getImageData() {
            const tempCanvas = document.createElement('canvas');
            tempCanvas.width = 28;
            tempCanvas.height = 28;
            const tempCtx = tempCanvas.getContext('2d');
            tempCtx.drawImage(this.canvas, 0, 0, 28, 28);
            const data = tempCtx.getImageData(0, 0, 28, 28).data;
            const input = [];
            for (let i = 0; i < data.length; i += 4) input.push(data[i] / 255.0);
            return input;
        }
    }

    /**
     * Handles Layer Builder UI.
     */
    class LayerBuilder {
        constructor(listId, controls) {
            this.listEl = document.getElementById(listId);
            this.controls = controls;
            this.layers = [];
            this.selectedIndex = -1;
            
            this.controls.addDense?.addEventListener('click', () => this.addLayer('Dense'));
            this.controls.addReLU?.addEventListener('click', () => this.addLayer('ReLU'));
            this.controls.addSigmoid?.addEventListener('click', () => this.addLayer('Sigmoid'));
            this.controls.addSoftmax?.addEventListener('click', () => this.addLayer('Softmax'));
            this.controls.moveUp?.addEventListener('click', () => this.moveLayer(-1));
            this.controls.moveDown?.addEventListener('click', () => this.moveLayer(1));
            this.controls.remove?.addEventListener('click', () => this.removeLayer());
            this.controls.reset?.addEventListener('click', () => this.setDefaultLayers());

            this.setDefaultLayers();
        }

        setDefaultLayers() {
            const hiddenSize = Number(document.getElementById('hiddenSize')?.value) || 100;
            this.layers = [
                { type: 'Dense', inputSize: 784, outputSize: hiddenSize },
                { type: 'ReLU' },
                { type: 'Dense', inputSize: hiddenSize, outputSize: 10 },
                { type: 'Softmax' }
            ];
            this.selectedIndex = -1;
            this.render();
        }

        addLayer(type) {
            const idx = this.selectedIndex >= 0 ? this.selectedIndex + 1 : this.layers.length;
            let newLayer = { type };
            if (type === 'Dense') {
                const prev = this.layers[idx - 1];
                const inputSize = (prev && prev.type === 'Dense') ? prev.outputSize : 100;
                newLayer = { type, inputSize, outputSize: 100 };
            }
            this.layers.splice(idx, 0, newLayer);
            this.selectedIndex = idx;
            this.render();
        }

        removeLayer() {
            if (this.selectedIndex >= 0) {
                this.layers.splice(this.selectedIndex, 1);
                this.selectedIndex = -1;
                this.render();
            }
        }

        moveLayer(dir) {
            const idx = this.selectedIndex;
            if (idx < 0) return;
            const newIdx = idx + dir;
            if (newIdx >= 0 && newIdx < this.layers.length) {
                [this.layers[idx], this.layers[newIdx]] = [this.layers[newIdx], this.layers[idx]];
                this.selectedIndex = newIdx;
                this.render();
            }
        }

        render() {
            this.listEl.innerHTML = '';
            this.layers.forEach((layer, idx) => {
                const row = document.createElement('div');
                row.className = `layer-row ${idx === this.selectedIndex ? 'selected' : ''}`;
                row.onclick = () => { this.selectedIndex = idx; this.render(); };

                const type = document.createElement('div');
                type.className = 'layer-type';
                type.textContent = `${idx + 1}. ${layer.type}`;
                row.appendChild(type);

                if (layer.type === 'Dense') {
                    const dims = document.createElement('div');
                    dims.className = 'layer-dims';
                    dims.innerHTML = `<input type="number" class="in" value="${layer.inputSize}"><span>→</span><input type="number" class="out" value="${layer.outputSize}">`;
                    const inInput = dims.querySelector('.in');
                    const outInput = dims.querySelector('.out');
                    inInput.onchange = (e) => layer.inputSize = Number(e.target.value);
                    outInput.onchange = (e) => {
                        layer.outputSize = Number(e.target.value);
                        this.propagateSize(idx, layer.outputSize);
                    };
                    inInput.onclick = outInput.onclick = (e) => e.stopPropagation();
                    row.appendChild(dims);
                }
                this.listEl.appendChild(row);
            });
        }

        propagateSize(idx, size) {
            for (let i = idx + 1; i < this.layers.length; i++) {
                if (this.layers[i].type === 'Dense') {
                    this.layers[i].inputSize = size;
                    this.render();
                    break;
                }
            }
        }

        validate() {
            const errs = [];
            if (!this.layers.length) return ['At least one layer required.'];
            if (this.layers[0].type !== 'Dense' || this.layers[0].inputSize !== 784) errs.push('First layer must be Dense(784).');
            if (this.layers[this.layers.length - 1].type !== 'Softmax') errs.push('Last layer must be Softmax.');
            let expectedIn = 784;
            this.layers.forEach((l, i) => {
                if (l.type === 'Dense') {
                    if (l.inputSize !== expectedIn) errs.push(`Layer ${i+1}: expected input ${expectedIn}, got ${l.inputSize}`);
                    expectedIn = l.outputSize;
                }
            });
            if (expectedIn !== 10) errs.push(`Final output must be 10, got ${expectedIn}`);
            return errs;
        }

        getConfig() {
            return this.layers.map(l => l.type === 'Dense' ? { type: 'Dense', inputSize: Number(l.inputSize), outputSize: Number(l.outputSize) } : { type: l.type });
        }
    }

    /**
     * Handles Charts using Chart.js.
     */
    class TrainingCharts {
        constructor(lossId, accId, logToggleId) {
            this.lossCtx = document.getElementById(lossId).getContext('2d');
            this.accCtx = document.getElementById(accId).getContext('2d');
            this.logToggle = document.getElementById(logToggleId);
            
            this.initCharts();
            
            this.logToggle?.addEventListener('change', () => {
                this.lossChart.options.scales.y.type = this.logToggle.checked ? 'logarithmic' : 'linear';
                this.lossChart.update();
            });
        }

        initCharts() {
            const commonOptions = {
                responsive: true,
                maintainAspectRatio: false,
                animation: false, // Disable animation for performance during streaming
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: { display: false }
                },
                elements: {
                    point: { radius: 0, hitRadius: 10 } // Hide points, show on hover
                }
            };

            this.lossChart = new Chart(this.lossCtx, {
                type: 'line',
                data: { labels: [], datasets: [{ label: 'Loss', data: [], borderColor: '#2196F3', borderWidth: 2, tension: 0.1 }] },
                options: {
                    ...commonOptions,
                    plugins: { title: { display: true, text: 'Training Loss' } },
                    scales: {
                        x: { title: { display: true, text: 'Epoch' }, ticks: { maxTicksLimit: 5 } },
                        y: { title: { display: true, text: 'Loss' } }
                    }
                }
            });

            this.accChart = new Chart(this.accCtx, {
                type: 'line',
                data: { labels: [], datasets: [{ label: 'Accuracy', data: [], borderColor: '#4CAF50', borderWidth: 2, tension: 0.1 }] },
                options: {
                    ...commonOptions,
                    plugins: { title: { display: true, text: 'Accuracy' } },
                    scales: {
                        x: { title: { display: true, text: 'Epoch' }, ticks: { maxTicksLimit: 5 } },
                        y: { title: { display: true, text: 'Accuracy' }, min: 0, max: 1 }
                    }
                }
            });
        }

        reset() {
            this.lossChart.data.labels = [];
            this.lossChart.data.datasets[0].data = [];
            this.accChart.data.labels = [];
            this.accChart.data.datasets[0].data = [];
            this.lossChart.update();
            this.accChart.update();
        }

        addPoint(epoch, loss, acc) {
            // Check if we need to decimate (prevent too many points)
            if (this.lossChart.data.labels.length > 500) {
                this.lossChart.data.labels.shift();
                this.lossChart.data.datasets[0].data.shift();
                this.accChart.data.labels.shift();
                this.accChart.data.datasets[0].data.shift();
            }

            this.lossChart.data.labels.push(epoch);
            this.lossChart.data.datasets[0].data.push(loss);
            
            this.accChart.data.labels.push(epoch);
            this.accChart.data.datasets[0].data.push(acc);
            
            // Throttle updates slightly if needed, but for now simple update
            this.lossChart.update('none');
            this.accChart.update('none');
        }
    }

    /**
     * Main App Logic.
     */
    class App {
        constructor() {
            this.canvas = new CanvasHandler('canvas', 'clearBtn');
            this.builder = new LayerBuilder('layerList', {
                addDense: document.getElementById('addDenseBtn'),
                addReLU: document.getElementById('addReLUBtn'),
                addSigmoid: document.getElementById('addSigmoidBtn'),
                addSoftmax: document.getElementById('addSoftmaxBtn'),
                moveUp: document.getElementById('moveUpBtn'),
                moveDown: document.getElementById('moveDownBtn'),
                remove: document.getElementById('removeBtn'),
                reset: document.getElementById('resetDefaultBtn')
            });
            
            this.charts = new TrainingCharts('lossChart', 'accChart', 'lossLogScale');
            
            this.initBars();
            
            document.getElementById('predictBtn')?.addEventListener('click', this.predict.bind(this));
            document.getElementById('startTrainBtn')?.addEventListener('click', this.startTraining.bind(this));
            this.canvas.clearBtn?.addEventListener('click', () => this.resetResults());
        }

        initBars() {
            const container = document.getElementById('bars');
            if(!container) return;
            container.innerHTML = '';
            for(let i=0; i<10; i++) {
                const w = document.createElement('div');
                w.className = 'bar-wrapper';
                w.innerHTML = `<div class="bar-value" id="val-${i}"></div><div class="bar" id="bar-${i}"></div><div class="bar-label">${i}</div>`;
                container.appendChild(w);
            }
        }

        async predict() {
            const btn = document.getElementById('predictBtn');
            const predEl = document.getElementById('prediction');
            const confEl = document.getElementById('confidence');
            
            try {
                btn.disabled = true;
                predEl.textContent = '...';
                const res = await fetch('/api/predict', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({image: this.canvas.getImageData()})
                });
                if(!res.ok) throw new Error('API Error');
                const result = await res.json();
                
                predEl.textContent = result.digit;
                confEl.textContent = `Confidence: ${(result.probabilities[result.digit]*100).toFixed(1)}%`;
                
                result.probabilities.forEach((p, i) => {
                    const bar = document.getElementById(`bar-${i}`);
                    const val = document.getElementById(`val-${i}`);
                    if(bar) {
                        const pct = p * 100;
                        bar.style.height = Math.max(2, pct) + '%';
                        bar.className = `bar ${i === result.digit ? 'active' : ''}`;
                        val.textContent = pct > 1 ? pct.toFixed(0)+'%' : '';
                        val.style.opacity = pct > 1 ? 1 : 0;
                    }
                });
            } catch(e) {
                console.error(e);
                predEl.textContent = '?';
            } finally {
                btn.disabled = false;
            }
        }

        resetResults() {
            document.getElementById('prediction').textContent = '-';
            document.getElementById('confidence').textContent = '';
            document.querySelectorAll('.bar').forEach(b => { b.style.height = '2%'; b.classList.remove('active'); });
            document.querySelectorAll('.bar-value').forEach(v => v.style.opacity = 0);
        }

        async startTraining() {
            const errors = this.builder.validate();
            if(errors.length) return alert(errors.join('\n'));
            
            const btn = document.getElementById('startTrainBtn');
            const statusEl = document.getElementById('trainStatus');
            
            const config = {
                layers: this.builder.getConfig(),
                config: {
                    epochs: Number(document.getElementById('epochs').value),
                    learningRate: Number(document.getElementById('learningRate').value),
                    trainSize: Number(document.getElementById('trainSize').value),
                    testSize: Number(document.getElementById('testSize').value)
                }
            };
            
            this.charts.reset();
            statusEl.textContent = 'Starting...';
            btn.disabled = true;
            
            try {
                const res = await fetch('/api/train/start', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(config)
                });
                if(!res.ok) throw new Error('Start Failed');
                const { jobId } = await res.json();
                
                if(!window.EventSource) return alert('SSE not supported');
                
                const es = new EventSource(`/api/train/stream?jobId=${jobId}`);
                es.onmessage = (ev) => {
                    const d = JSON.parse(ev.data);
                    if(d.error) {
                        statusEl.textContent = `Error: ${d.error}`;
                        es.close();
                        btn.disabled = false;
                        return;
                    }
                    
                    statusEl.textContent = `Ep: ${d.epoch || '-'} | Loss: ${d.loss?.toFixed(4) || '-'} | Acc: ${(d.accuracy*100)?.toFixed(1)}% | ${d.state}`;
                    
                    if(d.epoch != null && d.loss != null) {
                        this.charts.addPoint(d.epoch, d.loss, d.accuracy || 0);
                    }
                    
                    if(d.state === 'completed' || d.state === 'failed') {
                        es.close();
                        btn.disabled = false;
                    }
                };
                es.onerror = () => { es.close(); btn.disabled = false; statusEl.textContent += ' (Connection lost)'; };
                
            } catch(e) {
                statusEl.textContent = e.message;
                btn.disabled = false;
            }
        }
    }

    new App();
});
